/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center

import io.github.jisungbin.covid19center.datasource.DummyResponse
import io.github.jisungbin.covid19center.datasource.covidCenterHttpClientDefaultConfig
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterServerException
import io.github.jisungbin.covid19center.datasource.exception.CovidCenterUnauthorizedException
import io.github.jisungbin.covid19center.viewmodel.CovidCenterViewModel
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpStatusCode

class HttpClientTest : StringSpec() {
  init {
    coroutineTestScope = true

    "200 응답일 때 response가 정상적으로 파싱됨" {
      val mockEngine = MockEngine { respondOk(DummyResponse.Json) }
      val httpClient = HttpClient(mockEngine, block = HttpClientConfig<*>::covidCenterHttpClientDefaultConfig)

      val vm = CovidCenterViewModel(httpClient)

      vm.getCenterList(page = 1) shouldBe DummyResponse.CenterItemList
    }

    "401 응답일 때 exception이 정상적으로 출력됨" {
      val mockEngine = MockEngine { respondError(HttpStatusCode.Unauthorized) }
      val httpClient = HttpClient(mockEngine)

      val vm = CovidCenterViewModel(httpClient)

      shouldThrowWithMessage<CovidCenterUnauthorizedException>(HttpStatusCode.Unauthorized.description) {
        vm.getCenterList(page = 0)
      }
    }

    "500 응답일 때 exception이 정상적으로 출력됨" {
      val mockEngine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
      val httpClient = HttpClient(mockEngine)

      val vm = CovidCenterViewModel(httpClient)

      shouldThrowWithMessage<CovidCenterServerException>(HttpStatusCode.InternalServerError.description) {
        vm.getCenterList(page = 0)
      }
    }
  }
}
