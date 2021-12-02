package com.ariari.mowoori.ui.stamp.entity

import android.os.Parcelable
import com.ariari.mowoori.ui.register.entity.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailInfo(
    val userId: String = "",
    val userName: String = "",
    val missionId: String = "",
    val missionName: String = "",
    val detailMode: DetailMode = DetailMode.INQUIRY,
    val stamp: Stamp = Stamp(),
) : Parcelable
