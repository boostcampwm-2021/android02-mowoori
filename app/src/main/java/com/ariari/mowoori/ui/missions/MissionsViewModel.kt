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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionsRepository: MissionsRepository
) : ViewModel() {
    private val _plusBtnClick = MutableLiveData<Event<Boolean>>()
    val plusBtnClick: LiveData<Event<Boolean>> = _plusBtnClick

    private val _itemClick = MutableLiveData<Event<MissionInfo>>()
    val itemClick: LiveData<Event<MissionInfo>> = _itemClick

    private val _missionsType = MutableLiveData(Event(NOT_DONE_TYPE))
    val missionsType: LiveData<Event<Int>> = _missionsType

    private val _missionsList = MutableLiveData<List<Mission>>()
    val missionsList: LiveData<List<Mission>> = _missionsList

//    val tempNotDoneMissions = mutableListOf(
//        Mission("mission1", MissionInfo("미완료 미션1", "user1", 30, 10, 211101, 211201)),
//        Mission("mission2", MissionInfo("미완료 미션2", "user1", 20, 8, 211101, 211201))
//    )
//
//    val tempDoneMissions = mutableListOf(
//        Mission("mission3", MissionInfo("완료 미션1", "user1", 30, 30, 211101, 211201)),
//        Mission("mission4", MissionInfo("완료 미션2", "user1", 20, 20, 211101, 211201)),
//        Mission("mission5", MissionInfo("완료 미션3", "user1", 10, 10, 211101, 211201))
//    )
//
//    val tempFailMissions = mutableListOf(
//        Mission("mission6", MissionInfo("실패 미션1", "user1", 20, 8, 211101, 211001))
//    )

    fun setPlusBtnClick() {
        _plusBtnClick.value = Event(true)
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

    fun setMissionsList() {
        viewModelScope.launch {
            // TODO: groudId, userId 받아서 넣기
            val missionIdList = missionsRepository.getMissionIdList("groupId1")
            val missions = missionsRepository.getMissions("user1")
            _missionsList.value = when (requireNotNull(missionsType.value).peekContent()) {
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
                    Timber.d(getCurrentDate().toString())
                    missions.filter {
                        (missionIdList.contains(it.missionId)) &&
                                (getCurrentDate() > it.missionInfo.dueDate) &&
                                (it.missionInfo.curStamp < it.missionInfo.totalStamp)
                    }
                }
                else -> throw IllegalStateException()
            }
        }
    }

    fun setItemClick(missionInfo: MissionInfo) {
        Timber.d("item click")
        _itemClick.postValue(Event(missionInfo))
    }

    companion object {
        const val NOT_DONE_TYPE = 0
        const val DONE_TYPE = 1
        const val FAIL_TYPE = 2
    }
}
