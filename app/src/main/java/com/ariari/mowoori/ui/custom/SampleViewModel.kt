package com.ariari.mowoori.ui.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event

class SampleViewModel: ViewModel() {
    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> = _backBtnClick

    private val _plusBtnClick = MutableLiveData<Event<Boolean>>()
    val plusBtnClick: LiveData<Event<Boolean>> = _plusBtnClick

    private val _closeBtnClick = MutableLiveData<Event<Boolean>>()
    val closeBtnClick: LiveData<Event<Boolean>> = _closeBtnClick

    val backTitle = "뒤로가기"
    val closeTitle = "종료"
    val plusTitle = "추가"

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    fun setCloseBtnClick() {
        _closeBtnClick.value = Event(true)
    }

    fun setPlusBtnClick() {
        _plusBtnClick.value = Event(true)
    }
}
