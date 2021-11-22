package com.ariari.mowoori.data.remote.response

data class FcmResponse(
    val canonical_ids: Int,
    val failure: Int,
    val multicast_id: Long,
    val results: List<FcmResult>,
    val success: Int
)

data class FcmResult(
    val message_id: String
)

