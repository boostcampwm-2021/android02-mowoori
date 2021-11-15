package com.ariari.mowoori.ui.missions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionsRepository: MissionsRepository
) : ViewModel() {
    private val _plusBtnClick = MutableLiveData<Event<Boolean>>()
    val plusBtnClick: LiveData<Event<Boolean>> = _plusBtnClick

    private val _itemClick = MutableLiveData<Event<Mission>>()
    val itemClick: LiveData<Event<Mission>> = _itemClick

    private val _missionsType = MutableLiveData(Event(NOT_DONE_TYPE))
    val missionsType: LiveData<Event<Int>> = _missionsType

    private val _missionsList = MutableLiveData<List<Mission>>()
    val missionsList: LiveData<List<Mission>> = _missionsList

    private val _userName = MutableLiveData<Event<String>>()
    val userName: LiveData<Event<String>> get() = _userName

    fun setPlusBtnClick() {
        _plusBtnClick.value = Event(true)
    }

    fun setItemClick(mission: Mission) {
        _itemClick.postValue(Event(mission))
    }

    fun setNotDoneType() {
        _missionsType.value = Event(NOT_DONE_TYPE)
    }

    fun setDoneType() {
        _missionsType.value = Event(DONE_TYPE)
    }

    fun setFailType() {
        _missionsType.value = Event(FAIL_TYPE)
    }

    fun loadMissionsList() {
        viewModelScope.launch(Dispatchers.IO) {
            missionsRepository.getUser().onSuccess { user ->
                val missionIdList =
                    missionsRepository.getMissionIdList(user.userInfo.currentGroupId)
                val missions = missionsRepository.getMissions(user.userId)
                _missionsList.postValue(
                    when (requireNotNull(missionsType.value).peekContent()) {
                        NOT_DONE_TYPE -> {
                            missions.filter {
                                (missionIdList.contains(it.missionId)) &&
                                        (getCurrentDate() <= it.missionInfo.dueDate) &&
                                        (it.missionInfo.curStamp < it.missionInfo.totalStamp)
                            }
                        }
                        DONE_TYPE -> {
                            missions.filter {
                                (missionIdList.contains(it.missionId)) &&
                                        (it.missionInfo.curStamp == it.missionInfo.totalStamp)
                            }
                        }
                        FAIL_TYPE -> {
                            missions.filter {
                                (missionIdList.contains(it.missionId)) &&
                                        (getCurrentDate() > it.missionInfo.dueDate) &&
                                        (it.missionInfo.curStamp < it.missionInfo.totalStamp)
                            }
                        }
                        else -> throw IllegalStateException()
                    })
            }.onFailure {
                throw Exception("get User Exception!!")
            }
        }
    }

    fun loadUserName() {
        viewModelScope.launch(Dispatchers.IO) {
            missionsRepository.getUser()
                .onSuccess {
                    _userName.postValue(Event(it.userInfo.nickname))
                }
                .onFailure {
                    println("${it.message}")
                }
        }
    }

    companion object {
        const val NOT_DONE_TYPE = 0
        const val DONE_TYPE = 1
        const val FAIL_TYPE = 2
    }
}
