package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.register.entity.UserInfo


interface HomeRepository {
    fun getUserUid(): String?

    suspend fun getUserInfo(uid: String): Result<UserInfo>

    suspend fun getGroup(groupId: String): Result<Group>

    fun setCurrentGroupId(groupId: String)
}
