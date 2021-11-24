package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User


interface GroupRepository {
    suspend fun getGroupInfo(groupId: String): Result<GroupInfo>

    suspend fun isGroupNameExist(groupName: String): Result<Boolean>

    fun putGroupInfo(groupInfo: GroupInfo, user: User): Result<String>

    suspend fun addUserToGroup(groupId: String, user: User): Result<String>

    suspend fun getUser(): Result<User>

    suspend fun isExistGroupId(groupId: String): Result<Boolean>
}
