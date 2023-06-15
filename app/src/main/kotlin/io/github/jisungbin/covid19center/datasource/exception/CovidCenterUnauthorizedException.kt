/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.datasource.exception

import java.io.IOException

class CovidCenterUnauthorizedException(override val message: String) : IOException() {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is CovidCenterUnauthorizedException) return false

    return message == other.message
  }

  override fun hashCode() = message.hashCode()
}
