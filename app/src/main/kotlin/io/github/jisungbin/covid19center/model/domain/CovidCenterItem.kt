/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.model.domain

import io.github.jisungbin.covid19center.model.SimpleLatLng
import java.util.Date

data class CovidCenterItem(
  val id: Int,
  val centerType: String,
  val address: String,
  val latlng: SimpleLatLng,
  val centerName: String,
  val facilityName: String,
  val phoneNumber: String,
  val updatedAt: Date,
)
