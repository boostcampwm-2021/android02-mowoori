package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.ErrorMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject


class GroupRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
) : GroupRepository {
    override suspend fun getGroupInfo(groupId: String): Result<GroupInfo> = kotlin.runCatching {
        val snapshot = databaseReference.child("groups/$groupId").get().await()
        snapshot.getValue(GroupInfo::class.java)
            ?: throw NullPointerException(ErrorMessage.GroupInfo.message)
    }

    override suspend fun getGroupNameList(): Result<List<String>> = runCatching {
        val groupNameListSnapShot = databaseReference.child("groupNameList").get().await()
        groupNameListSnapShot.getValue(object : GenericTypeIndicator<List<String>>() {})
            ?: emptyList()
    }

    override fun putGroupInfo(
        groupNameList: List<String>,
        groupInfo: GroupInfo,
        user: User,
    ): Result<String> =
        kotlin.runCatching {
            val newId = databaseReference.child("groups").push().key
            newId?.let {
                val groupNameMutableList = groupNameList.toMutableList()
                if (groupNameMutableList.contains(groupInfo.groupName)) {
                    throw Exception(ErrorMessage.ExistGroupName.message)
                }
                groupNameMutableList.add(groupInfo.groupName)
                val tmpGroupList = user.userInfo.groupList
                    .toMutableList().apply { add(newId) }
                val newUserInfo =
                    user.userInfo.copy(groupList = tmpGroupList, currentGroupId = newId)
                val childUpdates = hashMapOf(
                    "/groupNameList/" to groupNameMutableList,
                    "/groups/$newId" to groupInfo,
                    "/users/${user.userId}" to newUserInfo
                )
                databaseReference.updateChildren(childUpdates)
                newId
            } ?: throw NullPointerException(ErrorMessage.PushKey.message)
        }

    override suspend fun addUserToGroup(groupId: String, user: User): Result<String> =
        kotlin.runCatching {
            val snapshot = databaseReference.child("groups/$groupId").get().await()
            val tmpGroup = snapshot.getValue(GroupInfo::class.java)
                ?: throw NullPointerException(ErrorMessage.GroupInfo.message)
            if (tmpGroup.userList.contains(user.userId)) {
                throw Exception(ErrorMessage.DuplicatedGroup.message)
            }
            val tmpUserList = tmpGroup.userList.toMutableList().apply { add(user.userId) }
            val newGroupInfo = tmpGroup.copy(userList = tmpUserList)

            val tmpGroupList = user.userInfo.groupList.toMutableList().apply { add(groupId) }
            val newUserInfo = user.userInfo.copy(groupList = tmpGroupList, currentGroupId = groupId)
            val childUpdates = hashMapOf(
                "/groups/$groupId" to newGroupInfo,
                "/users/${user.userId}" to newUserInfo
            )
            databaseReference.updateChildren(childUpdates)
            groupId
        }

    override suspend fun getUser(): Result<User> = kotlin.runCatching {
        val uid =
            firebaseAuth.currentUser?.uid ?: throw NullPointerException(ErrorMessage.Uid.message)
        val snapshot = databaseReference.child("users/$uid").get().await()
        val userInfo = snapshot.getValue(UserInfo::class.java)
            ?: throw NullPointerException(ErrorMessage.UserInfo.message)
        User(uid, userInfo)
    }

    override suspend fun isExistGroupId(groupId: String): Result<Boolean> = runCatching {
        val snapshot = databaseReference.child("groups/$groupId").get().await()
        snapshot.exists()
    }
}
