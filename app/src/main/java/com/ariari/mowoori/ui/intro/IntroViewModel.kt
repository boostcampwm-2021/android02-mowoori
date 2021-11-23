package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.Event
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val introRepository: IntroRepository,
) : ViewModel() {
    lateinit var auth: FirebaseAuth
        private set

    private val _isUserRegistered = MutableLiveData<Event<Boolean>>()
    val isUserRegistered: LiveData<Event<Boolean>> = _isUserRegistered

    private val _isTestLoginSuccess = MutableLiveData<Boolean>()
    val isTestLoginSuccess: LiveData<Boolean> = _isTestLoginSuccess

    private var fcmToken = ""

    private val _networkDialogEvent = MutableLiveData<Boolean>()
    val networkDialogEvent: LiveData<Boolean> get() = _networkDialogEvent

    private var _requestCount = 0
    private val requestCount get() = _requestCount

    private fun initRequestCount() {
        _requestCount = 0
    }

    private fun addRequestCount() {
        _requestCount++
    }

    private fun checkRequestCount() {
        if (requestCount == 1) {
            setNetworkDialogEvent()
        }
    }

    fun setFirebaseAuth() {
        auth = FirebaseAuth.getInstance()
    }

    fun setUserRegistered(isRegistered: Boolean) {
        introRepository.setUserRegistered(isRegistered)
    }

    fun getUserRegistered() = introRepository.getUserRegistered()

    private fun checkUserRegistered(userUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            introRepository.checkUserRegistered(userUid)
                .onSuccess {
                    _isUserRegistered.postValue(Event(it))
                }
                .onFailure {
                    addRequestCount()
                    checkRequestCount()
                }
        }
    }

    fun initFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            fcmToken = task.result.toString()
        })
    }

    fun updateFcmToken() {
        viewModelScope.launch(Dispatchers.IO) {
            introRepository.updateFcmToken(fcmToken)
        }
    }

    fun firebaseAuthWithGoogle(idToken: String?, testId: String = "", testPassword: String = "") {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            viewModelScope.launch(Dispatchers.IO) {
                initRequestCount()
                introRepository.signInWithCredential(auth, credential)
                    .onSuccess { uid ->
                        checkUserRegistered(uid)
                    }
                    .onFailure {
                        // TODO: uid is null
                        addRequestCount()
                        checkRequestCount()
                    }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                introRepository.signInWithEmailAndPassword(auth, testId, testPassword)
                    .onSuccess {
                        _isTestLoginSuccess.postValue(it)
                    }
                    .onFailure {
                        // 테스트 로그인은 네트워크 처리 x
                        _isTestLoginSuccess.postValue(false)
                    }
            }
        }
    }

    private fun setNetworkDialogEvent() {
        _networkDialogEvent.postValue(true)
    }
}
