/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.datasource.mapper

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.util.TimeZone

val objectMapper =
  ObjectMapper()
    .registerKotlinModule()
    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
    ?: error("ObjectMapper 생성에 실패했습니다.")
