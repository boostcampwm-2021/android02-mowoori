package com.ariari.mowoori.ui.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
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

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent

    fun createNickName() {
        viewModelScope.launch(Dispatchers.IO) {
            val nickname = introRepository.getRandomNickName()
            profileText.postValue(nickname)
        }
    }

    fun clickProfile() {
        _profileImageClickEvent.value = Event(Unit)
    }

    fun setProfileImage(uri: Uri) {
        _profileImageUri.postValue(uri)
    }

    fun registerUserInfo() {
        _loadingEvent.value = Event(true)
        val nickname = profileText.value ?: ""
        if (!checkNicknameValid(nickname)) {
            _invalidNicknameEvent.value = Event(Unit)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            var uploadUrl = ""
            profileImageUri.value?.let {
                uploadUrl = introRepository.putUserProfile(it)
            }
            val success = introRepository.userRegister(
                UserInfo(
                    nickname = nickname,
                    profileImage = uploadUrl
                )
            )
            _registerSuccessEvent.postValue(Event(success))
        }
    }

    private fun checkNicknameValid(nickname: String): Boolean {
        return (nickname.length <= 10 && nickname.isNotEmpty())
    }
}
