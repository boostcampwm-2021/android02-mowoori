package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.data.remote.datasource.FcmDataSource
import com.ariari.mowoori.data.remote.request.FcmData
import com.ariari.mowoori.data.remote.request.FcmRequest
import com.ariari.mowoori.data.remote.response.FcmResponse
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.ErrorMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class StampsRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth,
    private val fcmDataSource: FcmDataSource,
) : StampsRepository {

    override suspend fun getStampInfo(stampId: String): Result<StampInfo> = kotlin.runCatching {
        val snapshot = databaseReference.child("stamps/$stampId").get().await()
        snapshot.getValue(StampInfo::class.java)
            ?: throw NullPointerException(ErrorMessage.StampInfo.message)
    }

    override fun getUserId(): Result<String> = kotlin.runCatching {
        val user =
            firebaseAuth.currentUser ?: throw throw NullPointerException(ErrorMessage.Uid.message)
        user.uid
    }

    override suspend fun postStamp(stampInfo: StampInfo, mission: Mission): Result<String> =
        kotlin.runCatching {
            val newId = databaseReference.child("stamps").push().key
            newId?.let {
                val tmpStampList = mission.missionInfo.stampList.toMutableList().apply {
                    add(newId)
                }
                Timber.d(tmpStampList.toString())
                val childUpdates = hashMapOf(
                    "/missions/${mission.missionId}/curStamp" to mission.missionInfo.curStamp + 1,
                    "/missions/${mission.missionId}/stampList" to tmpStampList,
                    "/stamps/$newId" to stampInfo
                )
                databaseReference.updateChildren(childUpdates)
                newId
            } ?: throw NullPointerException(ErrorMessage.PushKey.message)
        }

    override suspend fun getMissionInfo(missionId: String): Result<MissionInfo> =
        kotlin.runCatching {
            val snapshot = databaseReference.child("missions/$missionId").get().await()
            snapshot.getValue(MissionInfo::class.java)
                ?: throw NullPointerException(ErrorMessage.MissionInfo.message)
        }

    override suspend fun putCertificationImage(uri: Uri, missionId: String): Result<String> =
        kotlin.runCatching {
            val ref = storageReference.child("images/$missionId/${uri.lastPathSegment}")
            val task = ref.putFile(uri).await()
            val uploadUri = task.storage.downloadUrl.await()
            Timber.d("putCert - $uploadUri")
            uploadUri.toString()
        }

    override suspend fun getStampImageUrl(uriString: String): Result<String> =
        kotlin.runCatching {
            Timber.i(uriString)
            Timber.i("error: ${storageReference.child("$uriString.jpg").downloadUrl.result}")
            storageReference.child("$uriString.jpg").downloadUrl.result.toString()
        }

    override suspend fun postFcmMessage(
        fcmToken: String,
        detailInfo: DetailInfo,
    ): Result<FcmResponse> =
        kotlin.runCatching {
            fcmDataSource.postFcmMessage(
                FcmRequest(
                    to = fcmToken,
                    priority = "high",
                    data = FcmData(
                        title = "그룹원의 미션인증",
                        body = "${detailInfo.userName}님이 [${detailInfo.missionName}]에 스탬프를 찍었어요!",
                        userName = detailInfo.userName,
                        missionName = detailInfo.missionName,
                        pictureUrl = detailInfo.stampInfo.pictureUrl,
                        comment = detailInfo.stampInfo.comment
                    )
                )
            )
        }

    override suspend fun getGroupMembersUserId(): Result<List<String>> =
        kotlin.runCatching {
            val uid =
                getUserId().getOrNull() ?: throw NullPointerException(ErrorMessage.Uid.message)
            val groupIdSnapshot = databaseReference.child("users/$uid/currentGroupId").get().await()
            val groupId = groupIdSnapshot.getValue<String>()
                ?: throw NullPointerException(ErrorMessage.CurrentGroupId.message)
            val userListSnapshot = databaseReference.child("groups/$groupId/userList").get().await()
            val userList = userListSnapshot.getValue<MutableList<String>>()
                ?: throw NullPointerException(ErrorMessage.UserList.message)
            userList.remove(uid)
            userList
        }

    override suspend fun getGroupMembersFcmToken(userId: String): Result<String> =
        kotlin.runCatching {
            val snapshot = databaseReference.child("users/$userId/fcmToken").get().await()
            snapshot.getValue<String>() ?: ""
        }

    override suspend fun putGroupDoneMission(): Result<Int> = runCatching {
        val uid =
            getUserId().getOrNull() ?: throw NullPointerException(ErrorMessage.Uid.message)
        val groupIdSnapshot = databaseReference.child("users/$uid/currentGroupId").get().await()
        val groupId = groupIdSnapshot.getValue<String>()
            ?: throw NullPointerException(ErrorMessage.CurrentGroupId.message)
        val doneMissionSnapshot =
            databaseReference.child("groups/$groupId/doneMission").get().await()
        val doneMission = doneMissionSnapshot.getValue<Int>()
            ?: throw NullPointerException(ErrorMessage.DoneMission.message)

        databaseReference.child("groups/$groupId/doneMission").setValue(doneMission + 1)
        doneMission + 1
    }
}
