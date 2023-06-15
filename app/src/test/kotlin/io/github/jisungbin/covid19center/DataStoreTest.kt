/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.jisungbin.covid19center

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import io.github.jisungbin.covid19center.datasource.CovidCenterDataStoreItemKey
import io.github.jisungbin.covid19center.datasource.DummyResponse
import io.github.jisungbin.covid19center.datasource.readCovidCenterDataAsImmutableListOrNull
import io.github.jisungbin.covid19center.datasource.writeCovidCenterData
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DataStoreTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()

  private val testCoroutineDispatcher = StandardTestDispatcher()
  private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

  private val testDataStoreFileName = "CovidCenterDataTestData"
  private val testDataStore =
    PreferenceDataStoreFactory.create(
      scope = testCoroutineScope,
      produceFile = { context.preferencesDataStoreFile(testDataStoreFileName) },
    )

  @Before
  fun setUp() {
    Dispatchers.setMain(testCoroutineDispatcher)
  }

  @Test
  fun writeCovidCenterData_NoException() = runTest {
    testDataStore.writeCovidCenterData(DummyResponse.CenterItemList)

    /* ktlint-disable max-line-length */
    val actualWritedJson =
      "[{\"id\":1,\"centerType\":\"청춘의\",\"address\":\"그들의 인간은 타오르고 할지라도 아니더면\",\"centerName\":\"사막이다.\",\"facilityName\":\"들은 바이며, 철환하였는가?\",\"phoneNumber\":\"02-1234-1234\",\"updatedAt\":1626378909000},{\"id\":2,\"centerType\":\"용기가\",\"address\":\"이상은 얼마나 커다란 충분히\",\"centerName\":\"이상의 물방아 있는가\",\"facilityName\":\"그와 꽃 청춘\",\"phoneNumber\":\"051-444-1111\",\"updatedAt\":1673506381000},{\"id\":3,\"centerType\":\"현저하게\",\"address\":\"할지라도 그것을 든 생의 그들을\",\"centerName\":\"들어 오아이스도 힘차게\",\"facilityName\":\"가장 창공에 황금시대다\",\"phoneNumber\":\"032-111-0000\",\"updatedAt\":1575030081000}]"
    /* ktlint-enable max-line-length */

    testDataStore.data.first()[CovidCenterDataStoreItemKey] shouldBe actualWritedJson
  }

  @Test
  fun readCovidCenterDataAsImmutableListOrNull_SuccessAndNotNull() = runTest {
    testDataStore.writeCovidCenterData(DummyResponse.CenterItemList)
    val result = testDataStore.readCovidCenterDataAsImmutableListOrNull()

    result shouldContainExactly DummyResponse.CenterItemList
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testCoroutineScope.runTest { testDataStore.edit(MutablePreferences::clear) }
    testCoroutineScope.cancel()
  }
}
