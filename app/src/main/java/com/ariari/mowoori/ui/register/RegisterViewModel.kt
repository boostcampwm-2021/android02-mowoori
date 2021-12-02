package com.ariari.mowoori.ui.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.DuplicatedException
import com.ariari.mowoori.util.InvalidMode
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
    private val _invalidNicknameEvent = MutableLiveData<InvalidMode>()
    val invalidNicknameEvent: LiveData<InvalidMode> = _invalidNicknameEvent

    private val _isRegisterSuccess = MutableLiveData<Boolean>()
    val isRegisterSuccess: LiveData<Boolean> = _isRegisterSuccess

    private val _profileImageClickEvent = MutableLiveData<Unit>()
    val profileImageClickEvent: LiveData<Unit> = _profileImageClickEvent

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> = _profileImageUri

    private var fcmToken = ""

    private val _loadingEvent = MutableLiveData<Boolean>()
    val loadingEvent: LiveData<Boolean> = _loadingEvent

    private val _isNetworkDialogShowed = MutableLiveData(false)
    val isNetworkDialogShowed: LiveData<Boolean> get() = _isNetworkDialogShowed

    fun setLoadingEvent(flag: Boolean) {
        _loadingEvent.postValue(flag)
    }

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = false
    }

    fun createNickName() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nickname = introRepository.getRandomNickName().getOrThrow()
                profileText.postValue(nickname)
            } catch (e: NullPointerException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            } catch (e: Exception) {
                checkNetworkDialog()
            }
        }
    }

    fun clickProfile() {
        _profileImageClickEvent.value = Unit
    }

    fun setProfileImage(uri: Uri) {
        _profileImageUri.postValue(uri)
    }

    fun setUserRegistered(isRegistered: Boolean) {
        introRepository.setUserRegistered(isRegistered)
    }

    fun initFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener
            fcmToken = task.result.toString()
        })
    }

    fun initFcmServerKey() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val key = introRepository.getFcmServerKey().getOrThrow()
            introRepository.updateFcmServerKey(key)
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        } catch (e: Exception) {
            checkNetworkDialog()
        }
    }

    fun registerUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        val nickname = profileText.value ?: ""
        if (!checkNicknameValid(nickname)) return@launch
        try {
            val uploadUrl = putUserProfile(profileImageUri.value)
            val userNameList = introRepository.getUserNameList().getOrThrow()
            val isSuccess =
                introRepository.registerUser(userNameList, UserInfo(nickname, uploadUrl, fcmToken))
                    .getOrThrow()
            _isRegisterSuccess.postValue(isSuccess)
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        } catch (e: DuplicatedException) {
            // 이름 중복 예외처리
            _invalidNicknameEvent.postValue(InvalidMode.AlreadyExistNickname)
            setLoadingEvent(false)
        } catch (e: Exception) {
            checkNetworkDialog()
        }
    }

    private fun checkNicknameValid(nickname: String): Boolean {
        return if (nickname.length <= 11 && nickname.isNotEmpty()) {
            true
        } else {
            setLoadingEvent(false)
            _invalidNicknameEvent.value = InvalidMode.InvalidNickname
            false
        }
    }

    private suspend fun putUserProfile(imageUri: Uri?): String {
        imageUri ?: return ""
        return introRepository.putUserProfile(imageUri).getOrThrow()
    }

    private fun checkNetworkDialog() {
        setLoadingEvent(false)
        _isNetworkDialogShowed.value?.let {
            if (!it) _isNetworkDialogShowed.postValue(true)
        }
    }
}
