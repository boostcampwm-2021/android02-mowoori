package com.ariari.mowoori.ui.stamp_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event

class StampDetailViewModel: ViewModel() {
    private val _closeBtnClick = MutableLiveData<Event<Boolean>>()
    val closeBtnClick: LiveData<Event<Boolean>> get() = _closeBtnClick

    fun setCloseBtnClick() {
        _closeBtnClick.value = Event(true)
    }
}
