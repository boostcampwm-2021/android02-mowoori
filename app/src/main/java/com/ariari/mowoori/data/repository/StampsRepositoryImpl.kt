package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class StampsRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth
) : StampsRepository {

    override suspend fun getStampInfo(stampId: String): Result<StampInfo> = kotlin.runCatching {
        val snapshot = databaseReference.child("stamps/$stampId").get().await()
        snapshot.getValue(StampInfo::class.java)
            ?: throw NullPointerException("getStampInfo is null")
    }

    override fun getUserId(): Result<String> = kotlin.runCatching {
        val user = firebaseAuth.currentUser ?: throw throw NullPointerException("getUserId is null")
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
            } ?: throw NullPointerException("Couldn't get push key for posts")
        }

    override suspend fun getMissionInfo(missionId: String): Result<MissionInfo> =
        kotlin.runCatching {
            val snapshot = databaseReference.child("missions/$missionId").get().await()
            snapshot.getValue(MissionInfo::class.java)
                ?: throw NullPointerException("getMissionInfo is null")
        }

    override suspend fun putCertificationImage(uri: Uri, missionId: String): Result<String> =
        runCatching {
            val ref = storageReference.child("images/$missionId/${uri.lastPathSegment}")
            val task = ref.putFile(uri).await()
            val uploadUri = task.storage.downloadUrl.await()
            Timber.i(uploadUri.toString())
            uploadUri.toString()
        }

    override suspend fun getStampImageUrl(uriString: String): Result<String> =
        kotlin.runCatching {
            Timber.i(uriString)
            Timber.i("error: ${storageReference.child("$uriString.jpg").downloadUrl.result}")
            storageReference.child("$uriString.jpg").downloadUrl.result.toString()
        }
}
