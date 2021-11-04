package com.ariari.mowoori.data.repository

import androidx.lifecycle.LiveData
import com.ariari.mowoori.util.Event
import com.google.firebase.database.DataSnapshot

interface IntroRepository {
    suspend fun checkUserRegistered(userUid: String): Boolean

    suspend fun getRandomNickName(): String

    fun getUserUid(): String?

    suspend fun userRegister(nickname: String): Boolean
}
