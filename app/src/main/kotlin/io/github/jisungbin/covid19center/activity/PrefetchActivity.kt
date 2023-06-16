/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.AndroidEntryPoint
import io.github.jisungbin.covid19center.datasource.dataStore
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterServerException
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterUnauthorizedException
import io.github.jisungbin.covid19center.datasource.removeCovidCenterData
import io.github.jisungbin.covid19center.datasource.writeCovidCenterData
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.util.ToastWrapper
import io.github.jisungbin.covid19center.util.checkNetworkIsAvailable
import io.github.jisungbin.covid19center.viewmodel.CovidCenterViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrefetchActivity : ComponentActivity() {

  private val toast by lazy { ToastWrapper(this) }

  @Inject
  lateinit var vm: CovidCenterViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!checkNetworkIsAvailable(applicationContext)) {
      toast("네트워크 연결이 필요합니다.")
      return finish()
    }

    setContent {
      MaterialTheme {
        val progressPercentAnimatable = remember {
          Animatable(
            initialValue = 0f,
            typeConverter = Float.VectorConverter,
            label = "PrefetchProgress",
          )
        }

        LaunchedEffect(Unit) {
          prefetchLaunchedEffect(
            dataStore = applicationContext.dataStore,
            animateProgressPercent = { targetValue, durationMillis ->
              progressPercentAnimatable.animateTo(
                targetValue = targetValue,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing),
              )
            },
            getCenterList = { pageIndex ->
              var result = listOf<CovidCenterItem>()
              try {
                result = vm.getCenterList(page = pageIndex)
              } catch (serverException: CovidCenterServerException) {
                toast("서버 요청에 실패했습니다.\n\n${serverException.message}")
              } catch (unauthorizedException: CovidCenterUnauthorizedException) {
                toast("API 인증에 실패했습니다.\n\n${unauthorizedException.message}")
              } catch (exception: IllegalStateException) {
                toast("응답 검증에 실패했습니다.\n\n${exception.message}")
              }
              result
            },
            onFinish = {
              changeActivityWithAnimation<MapActivity>()
            },
          )
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
          contentAlignment = Alignment.Center,
        ) {
          LinearProgressIndicator(progress = progressPercentAnimatable.value)
        }
      }
    }
  }
}

private inline fun <reified T : Activity> Activity.changeActivityWithAnimation() {
  startActivity(Intent(this, T::class.java))
  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
  finish()
}

@VisibleForTesting
fun CoroutineScope.prefetchLaunchedEffect(
  dataStore: DataStore<Preferences>,
  animateProgressPercent: suspend (targetValue: Float, durationMillis: Int) -> Unit,
  getCenterList: suspend (pageIndex: Int) -> List<CovidCenterItem>,
  onFinish: () -> Unit,
) {
  val jobs = ArrayList<Job>(10)
  val items = ArrayList<CovidCenterItem>(100)

  launch {
    dataStore.removeCovidCenterData()

    repeat(10) { pageIndex ->
      jobs += launch(Dispatchers.IO) {
        items += getCenterList(pageIndex)
      }
    }
  }

  // 1. 2초에 걸쳐 100%
  // 2. API 데이터 저장이 완료되지 않았다면 80%에서 대기
  // 3. 저장이 완료되면 0.4초에 걸쳐 100%
  launch {
    animateProgressPercent(/*targetValue = */ 0.8f, /*durationMillis = */ 1600)

    jobs.joinAll()
    dataStore.writeCovidCenterData(items)

    animateProgressPercent(/*targetValue = */ 1f, /*durationMillis = */ 400)
    onFinish()
  }
}
