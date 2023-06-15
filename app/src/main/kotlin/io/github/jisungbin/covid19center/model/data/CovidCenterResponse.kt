/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.model.data

import com.fasterxml.jackson.annotation.JsonProperty

data class CovidCenterResponse(
  @field:JsonProperty("perPage")
  val perPage: Int? = null,

  @field:JsonProperty("data")
  val data: List<CovidCenterItemData?>? = null,

  @field:JsonProperty("currentCount")
  val currentCount: Int? = null,

  @field:JsonProperty("matchCount")
  val matchCount: Int? = null,

  @field:JsonProperty("page")
  val page: Int? = null,

  @field:JsonProperty("totalCount")
  val totalCount: Int? = null,
)
