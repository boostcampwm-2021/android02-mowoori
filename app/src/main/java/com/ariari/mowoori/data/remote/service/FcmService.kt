package com.ariari.mowoori.data.remote.service

import com.ariari.mowoori.data.remote.request.FcmRequest
import com.ariari.mowoori.data.remote.response.FcmResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmService {
    @Headers("Authorization:key=AAAAfSwv9Lc:APA91bF1dqg9zinG1J5PhU9DMW6z0oQa9KLQRfSuSoAyTgu3VnNPUouuMdAClhyjD3VAa3YxESR76Myo2zYF4rTZG9he6560JumsAkuMY7nTFIwLQ89lXnvdaIxPg8pEiPHZGdFFN9WN")
    @POST("/fcm/send")
    suspend fun postMessage(
        @Body body: FcmRequest
    ): FcmResponse
}
