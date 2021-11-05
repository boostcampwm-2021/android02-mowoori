package com.ariari.mowoori.ui.register.entity

data class User(
    val userId: String,
    val userInfo: UserInfo
)

data class UserInfo(
    val nickName: String,
    val groupList: List<String> = emptyList(),
)
