package com.ariari.mowoori.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event

class StampsViewModel: ViewModel() {

    private val _spanCount = MutableLiveData<Event<Int>>()
    val spanCount: LiveData<Event<Int>> get() = _spanCount

    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> get() = _backBtnClick

    fun setSpanCount(result: Float) {
        _spanCount.value = Event(result.toInt())
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }
}
