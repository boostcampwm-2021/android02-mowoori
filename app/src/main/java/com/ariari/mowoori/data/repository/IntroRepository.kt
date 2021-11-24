package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

interface IntroRepository {
    fun setUserRegistered(isRegistered: Boolean)

    fun getUserRegistered(): Boolean

    suspend fun checkUserRegistered(userUid: String): Result<Boolean>

    suspend fun getRandomNickName(): Result<String>

    fun getUserUid(): String?

    suspend fun userRegister(userInfo: UserInfo): Result<Boolean>

    suspend fun putUserProfile(uri: Uri): Result<String>

    suspend fun updateFcmToken(token: String)

    suspend fun getFcmServerKey(): Result<String>

    suspend fun updateFcmServerKey(key: String)

    suspend fun signInWithCredential(auth: FirebaseAuth, credential: AuthCredential): Result<String>

    suspend fun signInWithEmailAndPassword(
        auth: FirebaseAuth,
        id: String,
        password: String,
    ): Result<Boolean>
}
