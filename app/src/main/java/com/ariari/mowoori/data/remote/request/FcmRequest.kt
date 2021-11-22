package com.ariari.mowoori.data.remote.request

data class FcmRequest(
    val `data`: Data,
    val priority: String,
    val to: String
)

data class Data(
    val title: String,
    val message: String
)
