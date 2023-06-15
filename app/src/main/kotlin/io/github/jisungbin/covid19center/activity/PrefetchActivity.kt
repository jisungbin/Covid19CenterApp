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
import io.github.jisungbin.covid19center.datasource.removeCovidCenterData
import io.github.jisungbin.covid19center.datasource.writeCovidCenterData
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.viewmodel.CovidCenterViewModel
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrefetchActivity : ComponentActivity() {

  @Inject
  lateinit var vm: CovidCenterViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
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
            getCenterList = { pageIndex -> vm.getCenterList(page = pageIndex) },
            changeActivity = ::changeActivityWithAnimation,
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

private fun Activity.changeActivityWithAnimation(activity: KClass<Activity>) {
  startActivity(Intent(this, activity.java))
  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

@VisibleForTesting
fun CoroutineScope.prefetchLaunchedEffect(
  dataStore: DataStore<Preferences>,
  animateProgressPercent: suspend (targetValue: Float, durationMillis: Int) -> Unit,
  getCenterList: suspend (pageIndex: Int) -> List<CovidCenterItem>,
  changeActivity: (activity: KClass<Activity>) -> Unit,
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
    animateProgressPercent(
      /*targetValue = */ 0.8f,
      /*durationMillis = */ 1600,
    )

    jobs.joinAll()
    dataStore.writeCovidCenterData(items)

    animateProgressPercent(
      /*targetValue = */ 1f,
      /*durationMillis = */ 400,
    )
    @Suppress("UNCHECKED_CAST")
    changeActivity(MapActivity::class as KClass<Activity>)
  }
}
