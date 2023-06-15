/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.viewmodel

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterServerException
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterUnauthorizedException
import io.github.jisungbin.covid19center.datasource.mapper.objectMapper
import io.github.jisungbin.covid19center.datasource.mapper.toDomain
import io.github.jisungbin.covid19center.model.data.CovidCenterResponse
import io.github.jisungbin.covid19center.model.domain.CovidCenterItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CovidCenterViewModel @Inject constructor(
  private val covidCenterHttpClient: HttpClient,
) {
  @Throws(CovidCenterUnauthorizedException::class, CovidCenterServerException::class)
  suspend fun getCenterList(page: Int, perPage: Int = 10): List<CovidCenterItem> {
    val httpResponse = covidCenterHttpClient.get {
      parameters {
        append("page", "$page")
        append("perPage", "$perPage")
      }
    }
    val (statusCode, statusDescription) = httpResponse.status

    return when (statusCode) {
      200 -> {
        val bodyResponse = httpResponse.bodyAsText()
        // Workaround for https://stackoverflow.com/q/65105118/14299073
        objectMapper.readValue<CovidCenterResponse>(bodyResponse).toDomain()
      }
      401 -> throw CovidCenterUnauthorizedException(statusDescription)
      500 -> throw CovidCenterServerException(statusDescription)
      else -> error("Unknown status: [$statusCode] $statusDescription")
    }
  }
}
