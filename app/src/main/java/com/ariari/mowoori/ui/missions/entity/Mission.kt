package com.ariari.mowoori.ui.missions.entity

data class Mission(
    val missionId: String,
    val missionInfo: MissionInfo
)

data class MissionInfo(
    val missionName: String = "",
    val userId: String = "",
    val totalStamp: Int,
    val curStamp: Int,
    val startDate: Int,
    val dueDate: Int
    // val stampList: List<Stamp> = emptyList()
)
