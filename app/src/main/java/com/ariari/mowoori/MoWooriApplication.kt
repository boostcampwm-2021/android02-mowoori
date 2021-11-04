package com.ariari.mowoori

import android.app.Application
import com.google.firebase.FirebaseApp


class MoWooriApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
