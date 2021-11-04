package com.ariari.mowoori.ui.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event

class SampleViewModel: ViewModel() {
    private val _backButtonClick = MutableLiveData<Event<Boolean>>()
    val backButtonClick: LiveData<Event<Boolean>> = _backButtonClick

    private val _plusButtonClick = MutableLiveData<Event<Boolean>>()
    val plusButtonClick: LiveData<Event<Boolean>> = _plusButtonClick

    private val _closeButtonClick = MutableLiveData<Event<Boolean>>()
    val closeButtonClick: LiveData<Event<Boolean>> = _closeButtonClick

    val backTitle = "뒤로가기"
    val closeTitle = "종료"
    val plusTitle = "추가"

    fun setBackButtonClick() {
        _backButtonClick.value = Event(true)
    }

    fun setCloseButtonClick() {
        _closeButtonClick.value = Event(true)
    }

    fun setPlusButtonClick() {
        _plusButtonClick.value = Event(true)
    }
}
