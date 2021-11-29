package com.ariari.mowoori.data.local.datasource

import android.content.SharedPreferences
import java.lang.NullPointerException
import javax.inject.Inject

class MoWooriPrefDataSourceImpl @Inject constructor(
    private val prefs: SharedPreferences
) : MoWooriPrefDataSource {

    override fun getUserRegistered(): Boolean = prefs.getBoolean(USER_REGISTERED_KEY, false)

    override fun setUserRegistered(boolean: Boolean) =
        prefs.edit().putBoolean(USER_REGISTERED_KEY, boolean).apply()

    override fun updateFcmServerKey(key: String) {
        prefs.edit().putString(FCM_SERVER_KEY, key).apply()
    }

    override fun getFcmServerKey(): String = prefs.getString(FCM_SERVER_KEY, "")
        ?: throw NullPointerException("fcmServerKey is null at sharedpreferences")

    companion object {
        private const val USER_REGISTERED_KEY = "USER_REGISTERED"
        private const val FCM_SERVER_KEY = "fcmServerKey"
    }
}
