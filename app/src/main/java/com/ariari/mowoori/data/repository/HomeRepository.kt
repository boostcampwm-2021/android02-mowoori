package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.register.entity.UserInfo


interface HomeRepository {
    fun getUserUid(): String?

    suspend fun getUserInfo(uid: String): Result<UserInfo>
}
