/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import dagger.hilt.android.AndroidEntryPoint
import io.github.jisungbin.covid19center.datasource.dataStore
import io.github.jisungbin.covid19center.datasource.removeCovidCenterData
import io.github.jisungbin.covid19center.datasource.writeCovidCenterData
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.github.jisungbin.covid19center.viewmodel.CovidCenterViewModel
import javax.inject.Inject
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
      LaunchedEffect(Unit) {
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
      }
    }
  }
}
