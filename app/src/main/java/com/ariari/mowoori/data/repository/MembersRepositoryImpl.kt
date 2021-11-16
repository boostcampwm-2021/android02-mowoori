package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MembersRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) : MembersRepository {
    override suspend fun getCurrentGroupInfo(): Result<Group> = kotlin.runCatching {
        val uid = getUserUid() ?: throw NullPointerException("userId is Null")
        val groupIdSnapshot = databaseReference.child("users/$uid/currentGroupId").get().await()
        val currentGroupId = groupIdSnapshot.getValue(String::class.java)
            ?: throw NullPointerException("groupId is Null")

        val groupInfoSnapshot = databaseReference.child("groups/$currentGroupId").get().await()
        val group = groupInfoSnapshot.getValue(GroupInfo::class.java) ?: throw NullPointerException(
            "groupInfo is Null"
        )
        Group(currentGroupId, group)
    }

    override fun getUserUid(): String? = firebaseAuth.currentUser?.uid

    override suspend fun getUserInfo(userId: String): User? =
        kotlin.runCatching {
            val userSnapshot = databaseReference.child("users/$userId").get().await()
            User(
                userId,
                userSnapshot.getValue(UserInfo::class.java) ?: UserInfo()
            )
        }.getOrNull()
}
