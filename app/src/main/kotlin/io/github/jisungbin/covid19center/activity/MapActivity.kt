/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:OptIn(ExperimentalNaverMapApi::class)

package io.github.jisungbin.covid19center.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.content.ContextCompat
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import io.github.jisungbin.covid19center.datasource.dataStore
import io.github.jisungbin.covid19center.datasource.mapper.objectMapper
import io.github.jisungbin.covid19center.datasource.mapper.toFormattedString
import io.github.jisungbin.covid19center.datasource.readCovidCenterDataAsImmutableListOrNull
import io.github.jisungbin.covid19center.model.asLatLng
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.model.domain.markerColorForCenterType
import io.github.jisungbin.covid19center.util.ToastWrapper
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val LocationIntervalMillis = 3000L
private const val PerfectCenterCount = 100

class MapActivity : ComponentActivity() {

  private val toast by lazy { ToastWrapper(this) }
  private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
  )
  private val locationPermissionRequest =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
      if (permissions.none { it.value }) toast("위치 권한이 모두 거부되었습니다.")
      else requestTrackingCurrentLocation()
    }

  private var lastLocation: Location? = null
  private val locationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }

  private val fusedLocationClient by lazy {
    LocationServices.getFusedLocationProviderClient(applicationContext)
  }
  private val locationCallback by lazy {
    object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        lastLocation = locationResult.lastLocation
      }
    }
  }

  override fun onRestart() {
    super.onRestart()
    // GPS 설정 갔다가 다시 돌아온 경우
    requestLocationUpdates()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestLocationPermissionIfNeededOrRequestTrackingLocation()

    setContent {
      val coroutineScope = rememberCoroutineScope()

      val cameraPositionState = rememberCameraPositionState()
      var centers by rememberSaveable<MutableState<ImmutableList<CovidCenterItem>>>(
        saver = Saver(
          save = { centersState ->
            objectMapper.writeValueAsString(centersState.value)
          },
          restore = { centersJson ->
            val centerList = objectMapper.readValue<List<CovidCenterItem>>(centersJson)
            mutableStateOf(centerList.toImmutableList())
          },
        ),
      ) {
        mutableStateOf(persistentListOf())
      }

      LaunchedEffect(Unit) {
        if (centers.isEmpty()) {
          centers = applicationContext.dataStore.readCovidCenterDataAsImmutableListOrNull()
            ?: persistentListOf()

          val centerCount = centers.size
          if (centerCount != PerfectCenterCount) {
            toast("Prefetch 과정이 제대로 진행되지 않았습니다. (센터 데이터 부족: $centerCount/$PerfectCenterCount)")
          }
        }
      }

      MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
          CovidCenterMap(
            centers = centers,
            cameraPositionState = cameraPositionState,
          )
          LocationOnButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            buttonIconTint = MaterialTheme.colorScheme.primary,
            onClick = {
              coroutineScope.launch {
                val lastLocation = lastLocation ?: return@launch toast("사용자의 위치를 조회할 수 없습니다.")
                val lastLatlng = LatLng(lastLocation.latitude, lastLocation.longitude)
                cameraPositionState.animate(update = CameraUpdate.scrollAndZoomTo(lastLatlng, 17.0))
              }
            },
          )
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  @Suppress("MissingPermission")
  private fun requestLocationUpdates() {
    fusedLocationClient.requestLocationUpdates(
      LocationRequest.Builder(LocationIntervalMillis).build(),
      locationCallback,
      Looper.getMainLooper(),
    )
  }

  private fun requestTrackingCurrentLocation() {
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      toast("위치 정보를 조회하기 위해 GPS 활성화가 필요합니다.")
      startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    } else {
      requestLocationUpdates()
    }
  }

  private fun locationPermissionNeededDialog(positiveButtonOnClick: DialogInterface.OnClickListener) {
    AlertDialog
      .Builder(this)
      .setTitle("위치 권한 필요")
      .setMessage("사용자의 위치를 조회하기 위해 위치 권한이 필요합니다.")
      .setPositiveButton("확인", positiveButtonOnClick)
      .show()
  }

  private fun requestLocationPermissionIfNeededOrRequestTrackingLocation() {
    val allPermissionDenied = locationPermissions.none { permission ->
      ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    if (allPermissionDenied) {
      locationPermissionNeededDialog { _, _ ->
        requestLocationPermission()
      }
    } else {
      requestTrackingCurrentLocation()
    }
  }

  private fun requestLocationPermission() {
    locationPermissionRequest.launch(locationPermissions)
  }
}

@Composable
private fun CovidCenterMap(
  modifier: Modifier = Modifier,
  centers: ImmutableList<CovidCenterItem>,
  cameraPositionState: CameraPositionState,
) {
  val activity = LocalContext.current as ComponentActivity
  val coroutineScope = rememberCoroutineScope()
  val mapUiSettings = remember {
    MapUiSettings(isZoomControlEnabled = false, isLogoClickEnabled = false)
  }

  val calender = remember { Calendar.getInstance() }
  val infoWindow = remember { InfoWindow() }

  var selectedMarker by /*rememberSaveable(
    saver = Saver(
      save = { markerState ->
        markerState.value?.let { marker ->
          listOf(marker.position, marker.icon)
        } ?: emptyList()
      },
      restore = { markerDatas ->
        val marker = if (markerDatas.isNotEmpty()) {
          val position = markerDatas[0] as LatLng
          val icon = markerDatas[1] as OverlayImage
          Marker(position, icon)
        } else {
          null
        }
        mutableStateOf(marker)
      },
    ),
  )*/ remember {
    mutableStateOf<Marker?>(null)
  }

  NaverMap(
    modifier = modifier.fillMaxSize(),
    uiSettings = mapUiSettings,
    locale = Locale.KOREA,
    cameraPositionState = cameraPositionState,
    /*onMapLoaded = {
      // 왜 마커 복원이 안 될까?
      @Suppress("NAME_SHADOWING")
      val selectedMarker = selectedMarker
      if (selectedMarker != null) {
        infoWindow.open(selectedMarker)
      }
    },*/
  ) {
    centers.fastForEach { center ->
      CovidCenterMarker(
        center = center,
        activityContext = activity,
        coroutineScope = coroutineScope,
        cameraPositionState = cameraPositionState,
        infoWindow = infoWindow,
        selectedMarker = selectedMarker,
        updateSelectedMarker = { marker ->
          selectedMarker = marker
        },
        checkIsValidDate = { date ->
          calender.apply { time = date }.get(Calendar.YEAR) >= 2019
        },
      )
    }
  }
}

// 이 친구는 안정으로 못 만들어서 아쉽다
@NaverMapComposable
@Composable
private fun CovidCenterMarker(
  center: CovidCenterItem,
  activityContext: Context,
  coroutineScope: CoroutineScope,
  cameraPositionState: CameraPositionState,
  infoWindow: InfoWindow,
  selectedMarker: Marker?,
  updateSelectedMarker: (marker: Marker?) -> Unit,
  checkIsValidDate: (date: Date) -> Boolean,
) {
  Marker(
    state = MarkerState(position = center.latlng.asLatLng()),
    icon = MarkerIcons.BLACK,
    iconTintColor = markerColorForCenterType(center.centerType),
    onClick = { marker ->
      if (selectedMarker != marker) {
        coroutineScope.launch {
          cameraPositionState.animate(CameraUpdate.scrollTo(marker.position))
        }
        updateSelectedMarker(marker)
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(activityContext) {
          override fun getText(infoWindow: InfoWindow) =
            buildString {
              with(center) {
                // 가끔씩 비어있는 데이터가 들어옴 -> 비어있지 않은 데이터만 표시
                if (centerName.isNotBlank()) {
                  append("[$centerName]")
                }
                if (address.isNotBlank()) {
                  appendLine()
                  append("- $address")
                }
                if (facilityName.isNotBlank()) {
                  appendLine()
                  append("- $facilityName")
                }
                if (phoneNumber.isNotBlank()) {
                  appendLine()
                  append("- $phoneNumber")
                }
                // 가끔씩 1900년도 데이터가 들어옴 -> 2019년 이상 데이터만 표시
                if (checkIsValidDate(updatedAt)) {
                  appendLine("\n")
                  append("마지막 업데이트: ${updatedAt.toFormattedString()}")
                }
              }
            }
        }
        infoWindow.open(marker)
        true
      } else {
        updateSelectedMarker(null)
        infoWindow.close()
        false
      }
    },
  )
}

@Composable
private fun LocationOnButton(
  modifier: Modifier = Modifier,
  buttonIconTint: Color = Color.Unspecified,
  onClick: () -> Unit,
) {
  Box(
    modifier = modifier
      .padding(30.dp)
      .size(50.dp)
      .shadow(elevation = 1.dp, shape = CircleShape)
      .background(color = Color.White, shape = CircleShape)
      .paint(
        painter = rememberVectorPainter(Icons.Filled.LocationOn),
        colorFilter = remember { ColorFilter.tint(color = buttonIconTint) },
      )
      .clickable(onClick = onClick),
  )
}
