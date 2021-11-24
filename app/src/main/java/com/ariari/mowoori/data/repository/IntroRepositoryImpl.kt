package com.ariari.mowoori.data.repository

import android.net.Uri
import com.ariari.mowoori.data.local.datasource.MoWooriPrefDataSource
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.ErrorMessage
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IntroRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseAuth: FirebaseAuth,
    private val preference: MoWooriPrefDataSource,
) : IntroRepository {
    override fun setUserRegistered(isRegistered: Boolean) {
        preference.setUserRegistered(isRegistered)
    }

    override fun getUserRegistered(): Boolean = preference.getUserRegistered()

    override suspend fun checkUserRegistered(userUid: String): Result<Boolean> = runCatching {
        val snapshot = firebaseReference.child("users").child(userUid).get().await()
        snapshot.value != null
    }

    override suspend fun getRandomNickName(): Result<String> = runCatching {
        val namesRef = firebaseReference.child("names").get().await()
        val map =
            namesRef.getValue(object : GenericTypeIndicator<HashMap<String, List<String>>>() {})
                ?: throw NullPointerException(ErrorMessage.Path.message)
        val prefixList = map["prefix"] ?: throw NullPointerException(ErrorMessage.HashKey.message)
        val nameList = map["name"] ?: throw NullPointerException(ErrorMessage.HashKey.message)
        "${prefixList.random()} ${nameList.random()}"
    }

    override fun getUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun userRegister(userInfo: UserInfo): Result<Boolean> = runCatching {
        val uid = getUserUid() ?: throw NullPointerException(ErrorMessage.Uid.message)
        firebaseReference.child("users").child(uid).setValue(userInfo)
        true
    }

    override suspend fun putUserProfile(uri: Uri): Result<String> = runCatching {
        val uid = getUserUid()
        val ref = storageReference.child("images/$uid/${uri.lastPathSegment}")
        val task = ref.putFile(uri).await()
        val uploadUrl = task.storage.downloadUrl.await()
        uploadUrl.toString()
    }

    override suspend fun updateFcmToken(token: String) {
        firebaseReference.child("users/${getUserUid()}/fcmToken").setValue(token)
    }

    override suspend fun signInWithCredential(
        auth: FirebaseAuth,
        credential: AuthCredential,
    ): Result<String> = kotlin.runCatching {
        val authResult = auth.signInWithCredential(credential).await()
        authResult.user?.uid ?: throw NullPointerException(ErrorMessage.Uid.message)
    }

    override suspend fun signInWithEmailAndPassword(
        auth: FirebaseAuth,
        id: String,
        password: String,
    ): Result<Boolean> = kotlin.runCatching {
        val authResult = auth.signInWithEmailAndPassword(id, password).await()
        authResult.user != null
    }
}
