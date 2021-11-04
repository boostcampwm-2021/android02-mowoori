package com.ariari.mowoori.ui.missions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event

class MissionsViewModel : ViewModel() {
    private val _missionsType = MutableLiveData(Event(NOT_DONE_TYPE))
    val missionsType: LiveData<Event<Int>> = _missionsType

    fun setNotDoneType() {
        _missionsType.value = Event(NOT_DONE_TYPE)
    }

    fun setDoneType() {
        _missionsType.value = Event(DONE_TYPE)
    }

    fun setFailType() {
        _missionsType.value = Event(FAIL_TYPE)
    }

    companion object {
        const val NOT_DONE_TYPE = 0
        const val DONE_TYPE = 1
        const val FAIL_TYPE = 2
    }
}
