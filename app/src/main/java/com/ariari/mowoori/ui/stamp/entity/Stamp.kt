package com.ariari.mowoori.ui.stamp.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Stamp(
    val stampId: String = "",
    val stampInfo: StampInfo,
)

@Parcelize
data class StampInfo(
    val pictureUrl: String = "",
    val comment: String = "",
    val timeStamp: Long = 0,
) : Parcelable
