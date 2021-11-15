package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.register.entity.User

interface MissionsRepository {
    // groups에서 해당 group에 있는 missionList get
    suspend fun getMissionIdList(groupId: String): List<String>

    // missions에서 mission 전부 다 들고오기 -> 이후 자기가 속한 group꺼인지 확인, userId도 확인
    suspend fun getMissions(userId: String): List<Mission>

    // get group
    suspend fun getUser(): Result<User>

    suspend fun isExistGroupId(groupId: String): Boolean

    // mission 추가
    suspend fun postMission(missionInfo: MissionInfo, groupId:String, missionIdList: List<String>)
    suspend fun getUserName(userId: String): Result<String>
}
