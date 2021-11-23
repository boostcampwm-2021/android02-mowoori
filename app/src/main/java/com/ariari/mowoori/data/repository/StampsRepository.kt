package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.data.remote.response.FcmResponse
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.StampInfo

interface StampsRepository {
    suspend fun getStampInfo(stampId: String): Result<StampInfo>
    fun getUserId(): Result<String>
    suspend fun getMissionInfo(missionId: String): Result<MissionInfo>
    suspend fun postStamp(stampInfo: StampInfo, mission: Mission): Result<String>

    suspend fun putCertificationImage(uri: Uri, missionId: String): Result<String>
    suspend fun getStampImageUrl(uriString: String): Result<String>

    suspend fun postFcmMessage(fcmToken: String): Result<FcmResponse>
    suspend fun getGroupMembersUserId(): Result<List<String>>
    suspend fun getGroupMembersFcmToken(userId: String): Result<String>
}
