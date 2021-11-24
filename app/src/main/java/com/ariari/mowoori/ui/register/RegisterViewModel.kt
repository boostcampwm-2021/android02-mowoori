package com.ariari.mowoori.ui.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val introRepository: IntroRepository,
) : ViewModel() {
    val profileText = MutableLiveData("")
    private val _invalidNicknameEvent = MutableLiveData<Event<Unit>>()
    val invalidNicknameEvent: LiveData<Event<Unit>> = _invalidNicknameEvent

    private val _registerSuccessEvent = MutableLiveData<Event<Boolean>>()
    val registerSuccessEvent: LiveData<Event<Boolean>> = _registerSuccessEvent

    private val _profileImageClickEvent = MutableLiveData<Event<Unit>>()
    val profileImageClickEvent: LiveData<Event<Unit>> = _profileImageClickEvent

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> = _profileImageUri

    private var fcmToken = ""

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent

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

    private fun setLoadingEvent(flag: Boolean) {
        _loadingEvent.postValue(Event(flag))
    }

    fun createNickName() {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            introRepository.getRandomNickName()
                .onSuccess { nickname ->
                    profileText.postValue(nickname)
                }
                .onFailure {
                    addRequestCount()
                    checkRequestCount()
                }
        }
    }

    fun clickProfile() {
        _profileImageClickEvent.value = Event(Unit)
    }

    fun setProfileImage(uri: Uri) {
        _profileImageUri.postValue(uri)
    }

    fun setUserRegistered(isRegistered: Boolean) {
        introRepository.setUserRegistered(isRegistered)
    }

    fun registerUserInfo() {
        setLoadingEvent(true)
        val nickname = profileText.value ?: ""
        if (!checkNicknameValid(nickname)) {
            setLoadingEvent(false)
            _invalidNicknameEvent.value = Event(Unit)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            var uploadUrl = ""
            profileImageUri.value?.let {
                introRepository.putUserProfile(it)
                    .onSuccess { url ->
                        uploadUrl = url
                    }
                    .onFailure {
                        addRequestCount()
                        checkRequestCount()
                        return@launch
                    }
            }
            initRequestCount()
            introRepository.userRegister(
                UserInfo(
                    nickname = nickname,
                    profileImage = uploadUrl,
                    fcmToken = fcmToken
                )
            ).onSuccess {
                setLoadingEvent(false)
                _registerSuccessEvent.postValue(Event(it))
            }.onFailure {
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

    fun initFcmServerKey() {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            introRepository.getFcmServerKey().onSuccess { key ->
                introRepository.updateFcmServerKey(key)
            }.onFailure {
                addRequestCount()
                checkRequestCount()
            }
        }
    }

    private fun checkNicknameValid(nickname: String): Boolean {
        return (nickname.length <= 11 && nickname.isNotEmpty())
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)
        _networkDialogEvent.postValue(true)
    }
}
