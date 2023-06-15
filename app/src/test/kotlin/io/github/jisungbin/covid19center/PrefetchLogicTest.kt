/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:Suppress("TestFunctionName", "NewApi")
@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.jisungbin.covid19center

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.jisungbin.covid19center.activity.prefetchLaunchedEffect
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.Enabled
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.io.path.createTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class PrefetchLogicTest : StringSpec() {
  init {
    val tempFile = createTempFile(suffix = ".preferences_pb").toFile()

    val testCoroutineDispatcher = StandardTestDispatcher()
    val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    val testDataStore =
      PreferenceDataStoreFactory.create(
        scope = testCoroutineScope,
        produceFile = { tempFile },
      )

    coroutineTestScope = true

    beforeSpec {
      Dispatchers.setMain(testCoroutineDispatcher)
    }

    afterSpec {
      Dispatchers.resetMain()
      tempFile.deleteRecursively()
      testCoroutineScope.cancel()
    }

    "prefetchLaunchedEffect 로직이 prefetch 애니메이션 조건에 맞게 작동함".config(
      enabledOrReasonIf = {
        Enabled.disabled("prefetchLaunchedEffect는 테스트 코드를 어떤 방식으로 작성해야 할지 모르겠음")
      },
    ) {
      var currentTargetValue: Float? = null
      var prefetchFinished = false

      launch(Dispatchers.IO) {
        prefetchLaunchedEffect(
          dataStore = testDataStore,
          animateProgressPercent = { targetValue, durationMillis ->
            currentTargetValue = targetValue
            delay(durationMillis.toLong())
          },
          getCenterList = { delay(3000); emptyList() },
          changeActivity = { prefetchFinished = true },
        )
      }

      launch(Dispatchers.IO) {
        delay(100)
        currentTargetValue shouldBe 0.8f
        prefetchFinished.shouldBeFalse()

        delay(2500) // current ms is 2600
        currentTargetValue shouldBe 0.8f
        prefetchFinished.shouldBeFalse()

        delay(700) // current ms is 3200
        currentTargetValue shouldBe 1f
        prefetchFinished.shouldBeFalse()

        delay(800) // current ms is 4000
        currentTargetValue shouldBe 1f
        prefetchFinished.shouldBeTrue()
      }
    }
  }
}
