package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.NullPointerException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) : HomeRepository {
    override fun getUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun getUserInfo(uid: String): Result<UserInfo> = kotlin.runCatching {
        val snapshot = firebaseReference.child("users/$uid").get().await()
        val userInfo = snapshot.getValue(UserInfo::class.java)
        userInfo ?: throw NullPointerException("userInfo is Null")
    }


}
