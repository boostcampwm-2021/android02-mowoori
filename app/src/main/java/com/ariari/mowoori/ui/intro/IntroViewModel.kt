package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val introRepository: IntroRepository,
) : ViewModel() {
    lateinit var auth: FirebaseAuth
        private set

    private val _isUserRegistered = MutableLiveData<Boolean>()
    val isUserRegistered: LiveData<Boolean> = _isUserRegistered

    private val _isFcmUpdated = MutableLiveData<Boolean>()
    val isFcmUpdated: LiveData<Boolean> = _isFcmUpdated

    private val _isTestLoginSuccess = MutableLiveData<Boolean>()
    val isTestLoginSuccess: LiveData<Boolean> = _isTestLoginSuccess

    private var fcmToken = ""

    private val _networkDialogEvent = MutableLiveData<Boolean>()
    val networkDialogEvent: LiveData<Boolean> get() = _networkDialogEvent

    private var requestCount = 0

    private fun initRequestCount() {
        requestCount = 0
    }

    private fun checkRequestCount() {
        requestCount++
        if (requestCount == 1) setNetworkDialogEvent()
    }

    fun setFirebaseAuth() {
        auth = FirebaseAuth.getInstance()
    }

    fun setUserRegistered(isRegistered: Boolean) {
        introRepository.setUserRegistered(isRegistered)
    }

    fun getUserRegistered() = introRepository.getUserRegistered()

    fun initFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            fcmToken = task.result.toString()
        })
    }

    fun updateFcmServerKeyAndFcmToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updateFcmServerKeyJob = updateFcmServerKey()
                val updateFcmTokenJob = updateFcmToken()
                joinAll(updateFcmServerKeyJob, updateFcmTokenJob)
                _isFcmUpdated.postValue(true)
            } catch (e: NullPointerException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            } catch (e: CancellationException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            }
        }
    }

    private suspend fun updateFcmServerKey() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val key = getFcmServerKey()
            // preference
            introRepository.updateFcmServerKey(key)
        } catch (e: Exception) {
            checkRequestCount()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    private suspend fun updateFcmToken() = viewModelScope.launch(Dispatchers.IO) {
        try {
            initRequestCount()
            // setValue
            introRepository.updateFcmToken(fcmToken)
        } catch (e: Exception) {
            checkRequestCount()
        }
    }

    private suspend fun getFcmServerKey(): String {
        initRequestCount()
        return introRepository.getFcmServerKey().getOrThrow()
    }

    fun firebaseAuthWithGoogle(idToken: String?, testId: String = "", testPassword: String = "") =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val uid = signInWithCredential(credential)
                    val isRegistered = checkUserRegistered(uid)
                    _isUserRegistered.postValue(isRegistered)
                } else {
                    // 테스트 아이디 로그인
                    val isSuccess = signInWithEmailAndPassword(testId, testPassword)
                    _isTestLoginSuccess.postValue(isSuccess)
                }
            } catch (e: FirebaseNetworkException) {
                checkRequestCount()
            } catch (e: NullPointerException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            }
        }

    private suspend fun signInWithCredential(credential: AuthCredential): String {
        initRequestCount()
        return introRepository.signInWithCredential(auth, credential).getOrThrow()
    }

    private suspend fun signInWithEmailAndPassword(testId: String, testPassword: String): Boolean {
        initRequestCount()
        return introRepository.signInWithEmailAndPassword(auth, testId, testPassword).getOrThrow()
    }

    private suspend fun checkUserRegistered(userUid: String): Boolean {
        initRequestCount()
        return introRepository.checkUserRegistered(userUid).getOrThrow()
    }

    private fun setNetworkDialogEvent() {
        _networkDialogEvent.postValue(true)
    }
}
