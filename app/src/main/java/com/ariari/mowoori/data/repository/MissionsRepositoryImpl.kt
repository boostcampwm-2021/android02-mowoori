package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.LogUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class MissionsRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
) : MissionsRepository {

    override suspend fun getMissionIdList(groupId: String): Result<List<String>> = runCatching {
        val groupsRef = firebaseReference.child("groups")
            .child(groupId)
            .child("missionList")
            .get().await()

        val missionIdList = groupsRef.getValue(object : GenericTypeIndicator<List<String>>() {})
        missionIdList ?: emptyList()
    }

    override suspend fun getMissions(userId: String): Result<List<Mission>> = runCatching {
        val missionList = mutableListOf<Mission>()
        val missionsRef = firebaseReference.child("missions")
            .get().await()

        missionsRef.children.forEach { dataSnapshot ->
            if (dataSnapshot.key != null) {
                val mission = Mission(
                    dataSnapshot.key as String,
                    dataSnapshot.getValue(MissionInfo::class.java) ?: MissionInfo()
                )
                if (mission.missionInfo.userId == userId) {
                    missionList.add(mission)
                }
            }
        }
        missionList
    }

    override suspend fun getUser(): Result<User> = kotlin.runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw NullPointerException("uid is null")
        val snapshot = firebaseReference.child("users/$uid").get().await()
        val userInfo = snapshot.getValue(UserInfo::class.java)
            ?: throw NullPointerException(ErrorMessage.UserInfo.message)
        User(uid, userInfo)
    }

    override suspend fun getMissionInfo(missionId: String): Result<MissionInfo> =
        kotlin.runCatching {
            val snapshot = firebaseReference.child("missions/$missionId").get().await()
            LogUtil.log("getMission Impl", snapshot.getValue(MissionInfo::class.java).toString())
            val missionInfo = snapshot.getValue(MissionInfo::class.java)
                ?: throw NullPointerException(ErrorMessage.MissionInfo.message)
            missionInfo
        }

    override suspend fun isExistGroupId(groupId: String): Boolean {
        val snapshot = firebaseReference.child("groups/$groupId").get().await()
        return snapshot.exists()
    }

    override suspend fun postMission(
        missionInfo: MissionInfo,
        groupId: String,
        missionIdList: List<String>,
    ): Result<Boolean> = runCatching {
        val missionId = firebaseReference.child("missions").push().key
        missionId?.let {
            val updatedMissionList = missionIdList.toMutableList().apply {
                add(missionId)
            }

            val childUpdates = hashMapOf(
                "missions/$missionId" to missionInfo,
                "groups/$groupId/missionList" to updatedMissionList
            )
            firebaseReference.updateChildren(childUpdates)
        }
        true
    }

    override suspend fun getUserName(userId: String): Result<String> = kotlin.runCatching {
        val snapshot = firebaseReference.child("users").child(userId).get().await()
        val userInfo = snapshot.getValue(UserInfo::class.java)
        userInfo?.nickname ?: throw NullPointerException(ErrorMessage.UserInfo.message)
    }
}
