package com.ariari.mowoori.ui.home.entity

data class Group(
    val groupId: String,
    val groupInfo: GroupInfo,
)

data class GroupInfo(
    val groupName: String = "",
    val userList: List<String> = emptyList(),
    val missionList: List<String> = emptyList(),
    var selected: Boolean = false,
)
