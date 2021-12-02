package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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

    private val _isNetworkDialogShowed = MutableLiveData(false)
    val isNetworkDialogShowed: LiveData<Boolean> get() = _isNetworkDialogShowed

    fun setFirebaseAuth() {
        auth = FirebaseAuth.getInstance()
    }

    fun setUserRegistered(isRegistered: Boolean) {
        introRepository.setUserRegistered(isRegistered)
    }

    fun getUserRegistered() = introRepository.getUserRegistered()

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = false
    }

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
            val updateFcmServerKeyJob = updateFcmServerKey()
            val updateFcmTokenJob = updateFcmToken()
            joinAll(updateFcmServerKeyJob, updateFcmTokenJob)
            if (!updateFcmServerKeyJob.isCancelled && !updateFcmTokenJob.isCancelled) {
                _isFcmUpdated.postValue(true)
            }
        }
    }

    private fun updateFcmServerKey() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val key = introRepository.getFcmServerKey().getOrThrow()
            // preference
            introRepository.updateFcmServerKey(key)
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        } catch (e: Exception) {
            checkNetworkDialog()
            this.cancel()
        }
    }

    private fun updateFcmToken() = viewModelScope.launch(Dispatchers.IO) {
        try {
            // setValue
            introRepository.updateFcmToken(fcmToken)
        } catch (e: Exception) {
            checkNetworkDialog()
            this.cancel()
        }
    }

    fun firebaseAuthWithGoogle(idToken: String?, testId: String = "", testPassword: String = "") =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val uid = introRepository.signInWithCredential(auth, credential).getOrThrow()
                    val isRegistered = introRepository.checkUserRegistered(uid).getOrThrow()
                    _isUserRegistered.postValue(isRegistered)
                } else {
                    // 테스트 아이디 로그인
                    val isSuccess =
                        introRepository.signInWithEmailAndPassword(auth, testId, testPassword)
                            .getOrThrow()
                    _isTestLoginSuccess.postValue(isSuccess)
                }
            } catch (e: FirebaseNetworkException) {
                checkNetworkDialog()
            } catch (e: NullPointerException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            } catch (e: Exception) {
                checkNetworkDialog()
            }
        }

    private fun checkNetworkDialog() {
        _isNetworkDialogShowed.value?.let {
            if (!it) _isNetworkDialogShowed.postValue(true)
        }
    }
}
