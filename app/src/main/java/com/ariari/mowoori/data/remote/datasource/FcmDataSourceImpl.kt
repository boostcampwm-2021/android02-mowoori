package com.ariari.mowoori.data.remote.datasource

import com.ariari.mowoori.data.remote.request.FcmRequest
import com.ariari.mowoori.data.remote.response.FcmResponse
import com.ariari.mowoori.data.remote.service.FcmService
import javax.inject.Inject

class FcmDataSourceImpl @Inject constructor(
    private val fcmService: FcmService
) : FcmDataSource {
    override suspend fun postFcmMessage(body: FcmRequest): FcmResponse =
        fcmService.postMessage(body)

}
