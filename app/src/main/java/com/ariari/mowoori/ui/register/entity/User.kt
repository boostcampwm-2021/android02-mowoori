package com.ariari.mowoori.ui.register.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val userInfo: UserInfo
) : Parcelable

@Parcelize
data class UserInfo(
    val nickname: String = "",
    val profileImage: String = "",
    val groupList: List<String> = emptyList(),
    val currentGroupId: String = ""
) : Parcelable
