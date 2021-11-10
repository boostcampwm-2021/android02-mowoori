package com.ariari.mowoori.ui.stamp.entity

data class Stamp(
    val stampId: String = "",
    val stampInfo: StampInfo,
)

data class StampInfo(
    val pictureUrl: String = "",
    val comment: String = "",
    val timeStamp: Long = 0,
)
