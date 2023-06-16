/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.model.domain

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import io.github.jisungbin.covid19center.model.SimpleLatLng
import java.util.Date
import kotlin.random.Random

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

private val Color.Companion.Random
  get(): Color {
    val random = Random(System.currentTimeMillis())

    val red = random.nextInt(256 / 2)
    val green = random.nextInt(256 / 2)
    val blue = random.nextInt(256 / 2)

    return Color(red = red, green = green, blue = blue)
  }
private val MarkerColors = mutableMapOf<String, Color>()

@Stable
fun markerColorForCenterType(centerType: String) =
  MarkerColors.getOrPut(centerType) { Color.Random }
