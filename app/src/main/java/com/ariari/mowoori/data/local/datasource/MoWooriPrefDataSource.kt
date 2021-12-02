package com.ariari.mowoori.data.local.datasource

interface MoWooriPrefDataSource {
    fun getUserRegistered(): Boolean
    fun setUserRegistered(boolean: Boolean)
    suspend fun updateFcmServerKey(key: String)
    fun getFcmServerKey():String
}
