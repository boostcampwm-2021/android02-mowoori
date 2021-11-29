package com.ariari.mowoori.data.remote.request

data class FcmRequest(
    val to: String,
    val priority: String,
    val `data`: FcmData
)

data class FcmData(
    val title: String,
    val body: String,
    val userId: String = "",
    val userName: String = "",
    val missionName: String = "",
    val pictureUrl: String = "",
    val comment: String = "",
)
