package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.home.entity.Group

interface MembersRepository {
    suspend fun getCurrentGroupInfo(): Result<Group>

    fun getUserUid(): String?
}
