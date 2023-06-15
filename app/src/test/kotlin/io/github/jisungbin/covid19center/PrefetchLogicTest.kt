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
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Ignore
import org.junit.Test

@Ignore("prefetchLaunchedEffect는 테스트 코드를 어떤 방식으로 작성해야 할지 모르겠음")
class PrefetchLogicTest {
  private val tempFile = createTempFile(suffix = ".preferences_pb").toFile()

  private val testCoroutineDispatcher = StandardTestDispatcher()
  private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

  private val testDataStore =
    PreferenceDataStoreFactory.create(
      scope = testCoroutineScope,
      produceFile = { tempFile },
    )

  @Test
  fun PrefetchLogicAnimationStrategyWorksFine_3sDelay() = runTest {
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

      delay(1000) // current ms is 4200
      currentTargetValue shouldBe 1f
      prefetchFinished.shouldBeTrue()
    }
  }

  @After
  fun tearDown() {
    tempFile.deleteRecursively()
    testCoroutineScope.cancel()
  }
}
