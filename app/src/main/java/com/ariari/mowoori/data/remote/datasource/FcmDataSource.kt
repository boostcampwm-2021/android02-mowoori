package com.ariari.mowoori.data.remote.datasource

import com.ariari.mowoori.data.remote.request.FcmRequest
import com.ariari.mowoori.data.remote.response.FcmResponse

interface FcmDataSource {
    suspend fun postFcmMessage(body:FcmRequest):FcmResponse
}
