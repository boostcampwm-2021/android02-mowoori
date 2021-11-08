package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.missions.entity.Mission

interface MissionsRepository {
    // groups에서 해당 group에 있는 missionList get
    suspend fun getMissionIdList(groupId: String): List<String>

    // missions에서 mission 전부 다 들고오기 -> 이후 자기가 속한 group꺼인지 확인, userId도 확인
    suspend fun getMissions(userId: String): List<Mission>
}
