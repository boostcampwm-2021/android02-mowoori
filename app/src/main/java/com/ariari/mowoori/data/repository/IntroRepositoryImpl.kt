package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IntroRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth
) : IntroRepository {
    override suspend fun checkUserRegistered(userUid: String): Boolean {
        val snapshot = firebaseReference.child("users").child(userUid).get().await()
        return snapshot.value != null
    }

    override suspend fun getRandomNickName(): String {
        val namesRef = firebaseReference.child("names").get().await()
        val map =
            namesRef.getValue(object : GenericTypeIndicator<HashMap<String, List<String>>>() {})
                ?: return ""
        val prefixList = map["prefix"] ?: return ""
        val nameList = map["name"] ?: return ""
        return "${prefixList.random()} ${nameList.random()}"
    }

    override fun getUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun userRegister(userInfo: UserInfo): Boolean {
        getUserUid()?.let {
            firebaseReference.child("users").child(it).setValue(userInfo)
            return true
        } ?: run { return false }
    }

    override suspend fun putUserProfile(uri: Uri): String {
        val uid = getUserUid()
        val ref = storageReference.child("images/$uid/${uri.lastPathSegment}")
        val task = ref.putFile(uri).await()
        val uploadUrl = task.storage.downloadUrl.await()
        return uploadUrl.toString()
    }

    override suspend fun updateFcmToken(token: String) {
        firebaseReference.child("users/${getUserUid()}/fcmToken").setValue(token)
    }
}
