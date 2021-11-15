package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.StampInfo

interface StampsRepository {
    suspend fun getStampInfo(stampId: String): Result<StampInfo>
    fun getUserId(): Result<String>
    suspend fun getMissionInfo(missionId: String): Result<MissionInfo>
    suspend fun postStamp(stampInfo: StampInfo, mission: Mission): Result<String>
}
