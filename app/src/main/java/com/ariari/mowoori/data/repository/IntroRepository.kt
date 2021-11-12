package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.ui.register.entity.UserInfo

interface IntroRepository {
    suspend fun checkUserRegistered(userUid: String): Boolean

    suspend fun getRandomNickName(): String

    fun getUserUid(): String?

    suspend fun userRegister(userInfo: UserInfo): Boolean

    suspend fun putUserProfile(uri: Uri): String
}
