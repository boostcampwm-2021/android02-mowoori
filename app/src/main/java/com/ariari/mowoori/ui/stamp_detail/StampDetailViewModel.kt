package com.ariari.mowoori.ui.stamp_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.util.Event

class StampDetailViewModel : ViewModel() {
    private val _closeBtnClick = MutableLiveData<Event<Boolean>>()
    val closeBtnClick: LiveData<Event<Boolean>> get() = _closeBtnClick

    private val _isCertify = MutableLiveData<Event<Boolean>>()
    val isCertify: LiveData<Event<Boolean>> get() = _isCertify

    private val _userName = MutableLiveData<Event<String>>()
    val userName: LiveData<Event<String>> get() = _userName

    private val _missionName = MutableLiveData<Event<String>>()
    val missionName: LiveData<Event<String>> get() = _missionName

    fun setCloseBtnClick() {
        _closeBtnClick.value = Event(true)
    }

    fun setUserName(userName: String) {
        _userName.value = Event(userName)
    }

    fun setMissionName(missionName: String) {
        _missionName.value = Event(missionName)
    }

    fun setIsCertify(detailMode: DetailMode) {
        println("StampDetail - $detailMode")
        when (detailMode) {
            DetailMode.INQUIRY -> _isCertify.value = Event(false)
            DetailMode.CERTIFY -> _isCertify.value = Event(true)
        }
    }
}
