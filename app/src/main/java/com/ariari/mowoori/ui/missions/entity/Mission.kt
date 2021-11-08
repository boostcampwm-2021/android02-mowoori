package com.ariari.mowoori.ui.missions.entity

data class Mission(
    val missionId: String = "",
    val missionInfo: MissionInfo
)

data class MissionInfo(
    val missionName: String = "",
    val userId: String = "",
    val totalStamp: Int = 0,
    val curStamp: Int = 0,
    val startDate: Int = 0,
    val dueDate: Int = 0,
    val stampList: List<String> = emptyList()
)
