package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.register.entity.User

interface MembersRepository {
    suspend fun getCurrentGroupInfo(): Result<Group>

    fun getUserUid(): String?

    suspend fun getUserInfo(userId:String): User?
}
