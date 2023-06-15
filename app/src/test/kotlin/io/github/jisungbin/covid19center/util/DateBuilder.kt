/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:Suppress("NewApi")

package io.github.jisungbin.covid19center.util

import java.util.Calendar
import java.util.Date

fun buildDate(
  year: Int = 0,
  month: Int = 0,
  day: Int = 0,
  hour: Int = 0,
  minute: Int = 0,
  second: Int = 0,
  millisecond: Int = 0,
): Date =
  Calendar
    .Builder()
    .setDate(year, month, day)
    .setTimeOfDay(hour, minute, second, millisecond)
    .build()
    .time
