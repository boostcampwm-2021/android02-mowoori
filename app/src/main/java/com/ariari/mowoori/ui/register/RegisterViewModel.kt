package com.ariari.mowoori.ui.register

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val introRepository: IntroRepository
) : ViewModel() {
    val profileText = ObservableField("")
    private val _invalidNicknameEvent = MutableLiveData<Event<Unit>>()
    val invalidNicknameEvent: LiveData<Event<Unit>> = _invalidNicknameEvent

    private val _registerSuccessEvent = MutableLiveData<Event<Boolean>>()
    val registerSuccessEvent: LiveData<Event<Boolean>> = _registerSuccessEvent

    fun createNickName() {
        viewModelScope.launch {
            val nickname = introRepository.getRandomNickName()
            profileText.set(nickname)
        }
    }

    fun clickProfile() {
        // TODO: 2021/11/04 이미지 가져오기
        val imageUri = ""
    }

    fun clickComplete() {
        val nickname = profileText.get() ?: ""
        if (!checkNicknameValid(nickname)) {
            _invalidNicknameEvent.value = Event(Unit)
            return
        }
        viewModelScope.launch {
            val success = introRepository.userRegister(nickname)
            _registerSuccessEvent.value = Event(success)
        }
    }

    private fun checkNicknameValid(nickname: String): Boolean {
        return (nickname.length <= 10 && nickname.isNotEmpty())
    }
}
