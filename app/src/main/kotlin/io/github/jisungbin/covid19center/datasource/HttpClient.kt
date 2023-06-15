/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.datasource

import androidx.annotation.VisibleForTesting
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jisungbin.covid19center.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path

private const val MaxTimeoutMillis = 3000L
private const val MaxRetryCount = 3

@VisibleForTesting
fun HttpClientConfig<*>.covidCenterHttpClientDefaultConfig() {
  defaultRequest {
    url {
      protocol = Credentials.Protocol
      host = Credentials.Host
      path(Credentials.Path)
      parameters.append("serviceKey", Credentials.ServiceKey)
    }
    contentType(ContentType.Application.Json)
  }
  // https://stackoverflow.com/q/65105118/14299073
  /*install(ContentNegotiation) {
    jackson {
      disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
      disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
  }*/
}

private val covidCenterHttpClient =
  HttpClient(engineFactory = CIO) {
    engine {
      endpoint {
        connectTimeout = MaxTimeoutMillis
        connectAttempts = MaxRetryCount
      }
    }
    covidCenterHttpClientDefaultConfig()
    install(Logging) {
      logger = object : Logger {
        override fun log(message: String) {
          if (BuildConfig.DEBUG) {
            println(message)
          }
        }
      }
      level = LogLevel.ALL
    }
  }

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object HttpClientProvider {
  @Provides
  fun provideCovidCenterHttpClient() = covidCenterHttpClient
}
