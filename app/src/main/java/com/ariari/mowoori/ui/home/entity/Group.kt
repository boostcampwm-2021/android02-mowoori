package com.ariari.mowoori.ui.home.entity

data class Group(
    val groupId: String,
    val groupInfo: GroupInfo,
    var selected: Boolean = false,
)

data class GroupInfo(
    val doneMission: Int = 0,
    val groupName: String = "",
    val userList: List<String> = emptyList(),
    val missionList: List<String> = emptyList(),
)
