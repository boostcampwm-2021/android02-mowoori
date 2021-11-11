package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {
    override fun getUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getUserInfo(uid: String): Result<UserInfo> = kotlin.runCatching {
        val snapshot = firebaseReference.child("users/$uid").get().await()
        val userInfo = snapshot.getValue(UserInfo::class.java)
        userInfo ?: throw NullPointerException("userInfo is Null")
    }

    override suspend fun getGroup(groupId: String): Result<Group> = kotlin.runCatching {
        val snapshot = firebaseReference.child("groups/$groupId").get().await()
        val groupInfo = snapshot.getValue(GroupInfo::class.java)
            ?: throw NullPointerException("groupInfo is Null")
        Group(groupId, groupInfo)
    }

    override fun setCurrentGroupId(groupId: String) {
        val uid = getUserUid()
        uid?.let { userId ->
            firebaseReference.child("users/$userId/currentGroupId").setValue(groupId)
        }
    }

}
