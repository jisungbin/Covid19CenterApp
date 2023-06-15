/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.datasource

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.jisungbin.covid19center.datasource.mapper.objectMapper
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.firstOrNull

private const val CovidCenterDataStoreKey = "CovidItems"

@VisibleForTesting
val CovidCenterDataStoreItemKey = stringPreferencesKey("CovidCenterDataStoreItem")

val Context.dataStore by preferencesDataStore(name = CovidCenterDataStoreKey)

suspend fun DataStore<Preferences>.removeCovidCenterData() {
  edit { preference ->
    preference.remove(CovidCenterDataStoreItemKey)
  }
}

suspend fun DataStore<Preferences>.writeCovidCenterData(items: List<CovidCenterItem>) {
  edit { preference ->
    preference[CovidCenterDataStoreItemKey] = objectMapper.writeValueAsString(items)
  }
}

suspend fun DataStore<Preferences>.readCovidCenterDataAsImmutableListOrNull(): ImmutableList<CovidCenterItem>? {
  val itemJson = data.firstOrNull()?.get(CovidCenterDataStoreItemKey) ?: return null
  return objectMapper.readValue<List<CovidCenterItem>>(itemJson).toImmutableList()
}
