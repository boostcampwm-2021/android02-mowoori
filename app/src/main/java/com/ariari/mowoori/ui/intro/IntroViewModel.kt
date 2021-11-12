package com.ariari.mowoori.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.Event
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

    fun checkUserRegistered(userUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isRegistered = introRepository.checkUserRegistered(userUid)
            _isUserRegistered.postValue(Event(isRegistered))
        }
    }
}
