package com.ariari.mowoori.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import timber.log.Timber

object NetworkCallBack : ConnectivityManager.NetworkCallback() {
    val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private var doAvailable: () -> Unit = {}
    private var doLost: () -> Unit = {}

    fun setDoAvailable(doAvailable: () -> Unit) {
        this.doAvailable = doAvailable
    }

    fun setDoLost(doLost: () -> Unit) {
        this.doLost = doLost
    }

    override fun onAvailable(network: Network) {
        Timber.d("available")
        doAvailable()
    }

    override fun onLost(network: Network) {
        Timber.d("lost")
        doLost()
    }
}
