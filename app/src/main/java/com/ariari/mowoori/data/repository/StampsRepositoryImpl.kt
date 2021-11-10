package com.ariari.mowoori.data.repository

import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StampsRepositoryImpl @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth,
) : StampsRepository {

    override suspend fun getStampInfo(stampId: String): Result<StampInfo> = kotlin.runCatching {
        val snapshot = databaseReference.child("stamps/$stampId").get().await()
        snapshot.getValue(StampInfo::class.java)
            ?: throw NullPointerException("getStampInfo is null")
    }

    override fun getUserId(): Result<String> = kotlin.runCatching {
        val user = firebaseAuth.currentUser ?: throw throw NullPointerException("getUserId is null")
        user.uid
    }
}
