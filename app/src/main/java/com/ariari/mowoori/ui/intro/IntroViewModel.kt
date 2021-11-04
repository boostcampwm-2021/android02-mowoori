package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.Event
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val introRepository: IntroRepository
) : ViewModel() {
    private val _isUserRegistered = MutableLiveData<Event<Boolean>>()
    val isUserRegistered: LiveData<Event<Boolean>> = _isUserRegistered

    fun checkUserRegistered(userUid: String) {
        _isUserRegistered.postValue(introRepository.checkUserRegistered(userUid).value)
    }
}
