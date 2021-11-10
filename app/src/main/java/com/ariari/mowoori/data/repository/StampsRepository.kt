package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.stamp.entity.StampInfo

interface StampsRepository {
    suspend fun getStampInfo(stampId: String): Result<StampInfo>
    fun getUserId(): Result<String>
}
