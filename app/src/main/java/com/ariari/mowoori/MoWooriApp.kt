package com.ariari.mowoori

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.ariari.mowoori.util.NetworkCallBack
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MoWooriApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .registerNetworkCallback(NetworkCallBack.networkRequest, NetworkCallBack)
    }
}
