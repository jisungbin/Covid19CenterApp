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
import dagger.hilt.android.AndroidEntryPoint
import io.github.jisungbin.covid19center.datasource.dataStore
import io.github.jisungbin.covid19center.datasource.removeCovidCenterData
import io.github.jisungbin.covid19center.datasource.writeCovidCenterData
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.viewmodel.CovidCenterViewModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

private const val PrefetchTimeoutMillis = 4000L

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
          var covidCenterDataSaveComplete = false
          var progressContinuation: CancellableContinuation<Unit>? = null

          launch {
            applicationContext.dataStore.removeCovidCenterData()

            val jobs = ArrayList<Job>(10)
            val items = ArrayList<CovidCenterItem>(100)
            repeat(10) { pageIndex ->
              jobs += launch(Dispatchers.IO) {
                items += vm.getCenterList(page = pageIndex)
              }
            }

            jobs.joinAll()
            applicationContext.dataStore.writeCovidCenterData(items)
            covidCenterDataSaveComplete = true
            progressContinuation?.resume(Unit)
          }

          // 1. 2초에 걸쳐 100%
          // 2. API 데이터 저장이 완료되지 않았다면 80%에서 대기
          // 3. 저장이 완료되면 0.4초에 걸쳐 100%
          launch {
            progressPercentAnimatable.animateTo(
              targetValue = 0.8f,
              animationSpec = tween(durationMillis = 1600, easing = LinearEasing),
            )

            suspend fun toFinishProgress() {
              // LinearEasing이라 velocity 복원 불필요
              progressPercentAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing),
              )
              changeActivityWithAnimation<MapActivity>()
            }

            if (covidCenterDataSaveComplete) {
              toFinishProgress()
            } else {
              withTimeout(PrefetchTimeoutMillis) {
                @Suppress("RemoveExplicitTypeArguments")
                suspendCancellableCoroutine<Unit> { continuation ->
                  progressContinuation = continuation
                }
              }
              toFinishProgress()
            }
          }
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

private inline fun <reified T : Activity> Activity.changeActivityWithAnimation(
  intentBuilder: Intent.() -> Intent = { this },
) {
  startActivity(intentBuilder(Intent(this, T::class.java)))
  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}
