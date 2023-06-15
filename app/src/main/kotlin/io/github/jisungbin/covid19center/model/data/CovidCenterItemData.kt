/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class CovidCenterItemData(
  @field:JsonProperty("zipCode")
  val zipCode: String? = null,

  @field:JsonProperty("address")
  val address: String? = null,

  @field:JsonProperty("sigungu")
  val sigungu: String? = null,

  @field:JsonProperty("lng")
  val lng: String? = null,

  @field:JsonProperty("org")
  val org: String? = null,

  @field:JsonProperty("centerType")
  val centerType: String? = null,

  @field:JsonProperty("createdAt")
  val createdAt: String? = null,

  @field:JsonProperty("phoneNumber")
  val phoneNumber: String? = null,

  @field:JsonProperty("sido")
  val sido: String? = null,

  @field:JsonProperty("facilityName")
  val facilityName: String? = null,

  @field:JsonProperty("id")
  val id: Int? = null,

  @field:JsonProperty("lat")
  val lat: String? = null,

  @field:JsonProperty("centerName")
  val centerName: String? = null,

  @field:JsonProperty("updatedAt")
  val updatedAt: String? = null,
)
