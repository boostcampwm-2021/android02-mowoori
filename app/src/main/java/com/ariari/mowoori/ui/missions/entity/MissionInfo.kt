package com.ariari.mowoori.ui.missions.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MissionInfo(
    val missionName: String = "",
    val userId: String = "",
    val totalStamp: Int = 0,
    val curStamp: Int = 0,
    val startDate: Int = 0,
    val dueDate: Int = 0,
    val stampList: List<String> = emptyList()
) : Parcelable
