package com.ariari.mowoori.data.local.datasource

interface MoWooriPrefDataSource {
    fun getUserRegistered(): Boolean
    fun setUserRegistered(boolean: Boolean)
    fun updateFcmServerKey(key: String)
}
