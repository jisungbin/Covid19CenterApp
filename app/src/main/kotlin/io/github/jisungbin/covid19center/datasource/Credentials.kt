/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:Suppress("SpellCheckingInspection")

package io.github.jisungbin.covid19center.datasource

import io.ktor.http.URLProtocol

object Credentials {
  val Protocol = URLProtocol.HTTPS
  const val Host = "api.odcloud.kr"
  const val Path = "api/15077586/v1/centers"
  const val ServiceKey =
    "bNmSjmL3NWL/mAmsQV0SyDT+8DCdZckhVg5/tSsmJHa47eBZBE+aFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg=="
}
