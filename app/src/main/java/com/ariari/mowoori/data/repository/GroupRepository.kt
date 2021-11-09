package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.register.entity.UserInfo


interface GroupRepository {
    suspend fun getGroupInfo(groupId: String): Result<GroupInfo>

    fun putGroupInfo(groupInfo: GroupInfo, user: User): Result<String>

    suspend fun addUserToGroup(groupId: String, user: User): Result<String>

    suspend fun getUser(): Result<User>

    suspend fun isExistGroupId(groupId: String): Boolean
}
