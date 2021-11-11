package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.UserInfo


interface HomeRepository {
    fun getUserUid(): String?

    suspend fun getUserInfo(uid: String): Result<UserInfo>

    suspend fun getGroupInfo(groupId: String): Result<GroupInfo>
}
