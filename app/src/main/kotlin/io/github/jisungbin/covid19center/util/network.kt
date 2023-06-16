/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

package io.github.jisungbin.covid19center.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun checkNetworkIsAvailable(context: Context): Boolean {
  val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

  return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}
