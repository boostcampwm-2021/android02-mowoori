package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.Event
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val introRepository: IntroRepository
) : ViewModel() {
    private val _isUserRegistered = MutableLiveData<Event<Boolean>>()
    val isUserRegistered: LiveData<Event<Boolean>> = _isUserRegistered

    private var fcmToken = ""

    fun setUserRegistered(isRegistered:Boolean){
        introRepository.setUserRegistered(isRegistered)
    }

    fun getUserRegistered() = introRepository.getUserRegistered()

    fun checkUserRegistered(userUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isRegistered = introRepository.checkUserRegistered(userUid)
            _isUserRegistered.postValue(Event(isRegistered))
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
        viewModelScope.launch(Dispatchers.IO) { introRepository.updateFcmToken(fcmToken) }
    }

}
