package com.ariari.mowoori.data.remote.service

import com.ariari.mowoori.data.remote.request.FcmRequest
import com.ariari.mowoori.data.remote.response.FcmResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmService {
    @POST("/fcm/send")
    suspend fun postMessage(
        @Body body: FcmRequest
    ): FcmResponse
}
