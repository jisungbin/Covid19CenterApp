/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.model

import com.naver.maps.geometry.LatLng

/** 역직렬화가 쉽게 가능하도록 [LatLng]의 심플 버전을 제공합니다. */
data class SimpleLatLng(val latitude: Double, val longitude: Double)
