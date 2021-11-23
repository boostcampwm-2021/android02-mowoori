package com.ariari.mowoori.util

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import timber.log.Timber

object NetworkCallBack : ConnectivityManager.NetworkCallback() {
    val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private var doAvailable: () -> Unit = {}
    private var doLost: () -> Unit = {}
    private var doCellularChanged: () -> Unit = {}

    fun setDoAvailable(doAvailable: () -> Unit) {
        this.doAvailable = doAvailable
    }

    fun setDoLost(doLost: () -> Unit) {
        this.doLost = doLost
    }

    fun setDoCellularChanged(doCellularChanged: () -> Unit) {
        this.doCellularChanged = doCellularChanged
    }

    override fun onAvailable(network: Network) {
        Timber.d("available")
        doAvailable()
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Timber.d("WIFI")
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Timber.d("CELLULAR")
                doCellularChanged()
            }
            else -> Unit
        }
    }

    override fun onLost(network: Network) {
        Timber.d("lost")
        doLost()
    }
}
