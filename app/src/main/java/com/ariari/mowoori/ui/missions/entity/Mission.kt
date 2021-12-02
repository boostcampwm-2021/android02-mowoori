package com.ariari.mowoori.ui.missions.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mission(
    val missionId: String = "",
    val missionInfo: MissionInfo
) : Parcelable
