package com.ariari.mowoori.ui.stamp.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stamp(
    val stampId: String = "",
    val stampInfo: StampInfo = StampInfo(),
) : Parcelable

@Parcelize
data class StampInfo(
    val pictureUrl: String = "",
    val comment: String = "",
    val timeStamp: Int = 0,
) : Parcelable
