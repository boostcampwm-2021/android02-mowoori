package com.ariari.mowoori.data.preference

import android.content.SharedPreferences
import javax.inject.Inject

class MoWooriPreferenceImpl @Inject constructor(
    private val prefs: SharedPreferences
) : MoWooriPreference {

    override fun getUserRegistered(): Boolean = prefs.getBoolean(USER_REGISTERED_KEY, false)
    override fun setUserRegistered(boolean: Boolean) =
        prefs.edit().putBoolean(USER_REGISTERED_KEY, boolean).apply()


    companion object {
        private const val USER_REGISTERED_KEY = "USER_REGISTERED"
    }
}
