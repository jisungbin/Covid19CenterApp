/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:OptIn(ExperimentalContracts::class)

package io.github.jisungbin.covid19center.datasource.mapper

import androidx.compose.ui.util.fastMap
import io.github.jisungbin.covid19center.model.SimpleLatLng
import io.github.jisungbin.covid19center.model.data.CovidCenterResponse
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun CovidCenterResponse.toDomain() =
  ensureValid(data).fastMap { item ->
    ensureValid(item)
    CovidCenterItem(
      id = ensureValid(item.id),
      centerType = ensureValid(item.centerType),
      address = ensureValid(item.address),
      latlng = SimpleLatLng(ensureValid(item.lat?.toDoubleOrNull()), ensureValid(item.lng?.toDoubleOrNull())),
      centerName = ensureValid(item.centerName),
      facilityName = ensureValid(item.facilityName),
      phoneNumber = ensureValid(item.phoneNumber),
      updatedAt = ensureValid(item.updatedAt).toDate(),
    )
  }

private fun <T> ensureValid(data: T?): T {
  contract { returns() implies (data != null) }
  if (data == null) error("필수 응답이 누락되었습니다.")
  return data
}

// 2021-03-15 00:03:43
private const val CovidCenterDateFormat = "yyyy-MM-dd HH:mm:ss"
private val CovidCenterDateFormatter = SimpleDateFormat(CovidCenterDateFormat, Locale.KOREA)

private fun String.toDate() =
  CovidCenterDateFormatter.parse(this) ?: error("날짜 형식이 잘못되었습니다. ($this)")
