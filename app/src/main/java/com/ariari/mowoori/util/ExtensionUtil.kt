package com.ariari.mowoori.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun Context.isNetWorkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // 현재 활성화된 네트워크 반환! 없으면 null
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    // permission.ACCESS_NETWORK_STATE 을 추가해야 getNetworkCapabilities 함수를 원활히 사용할 수 있다.
    val networkCapabilities =
        connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return when {
        // 와이파이 transport 를 사용하고 있음을 나타낸다.
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}
