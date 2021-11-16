package com.ariari.mowoori.ui.stamp.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailInfo(
    val userName: String,
    val missionId: String,
    val missionName: String,
    val detailMode: DetailMode,
    val stampInfo: StampInfo,
) : Parcelable
