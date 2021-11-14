package com.ariari.mowoori.ui.missions_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.getCurrentDate
import com.ariari.mowoori.util.getCurrentDatePlusMonths
import com.ariari.mowoori.util.getMissionIntFormatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionsAddViewModel @Inject constructor(
    private val missionsRepository: MissionsRepository
) : ViewModel() {
    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> = _backBtnClick

    private val _numberCountClick = MutableLiveData<Event<Unit>>()
    val numberCountClick: LiveData<Event<Unit>> = _numberCountClick

    private val _missionCount = MutableLiveData<Int>()
    val missionCount: LiveData<Int> = _missionCount

    private val _missionStartDate = MutableLiveData<Int>()
    val missionStartDate: LiveData<Int> = _missionStartDate

    private val _missionEndDate = MutableLiveData<Int>()
    val missionEndDate: LiveData<Int> = _missionEndDate

    private val _checkMissionValidEvent = MutableLiveData<Event<Unit>>()
    val checkMissionValidEvent: LiveData<Event<Unit>> = _checkMissionValidEvent

    private val _isMissionPosted = MutableLiveData<Event<Unit>>()
    val isMissionPosted: LiveData<Event<Unit>> = _isMissionPosted

    init {
        _missionStartDate.value = getCurrentDate()
        _missionEndDate.value = getCurrentDatePlusMonths(1)
        updateMissionCount(10)
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    fun postMission(missionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            missionsRepository.getUser().onSuccess { user ->
                // missionIdList에 항목 추가
                var missionIdList =
                    missionsRepository.getMissionIdList(user.userInfo.currentGroupId)
                val mission = createMission(user.userId, missionName)

                if (missionIdList.isEmpty()) missionIdList = mutableListOf()
                (missionIdList as MutableList).add(mission.missionId)
                missionsRepository.postMissionIdList(user.userInfo.currentGroupId, missionIdList)

                // missions에 mission 추가
                missionsRepository.postMission(mission)

                // 화면 종료 Event 실행
                _isMissionPosted.postValue(Event(Unit))
            }.onFailure {
                throw Exception("get User Exception!!")
            }
        }
    }

    private fun createMission(userId: String, missionName: String): Mission {
        return Mission(
            missionName,
            MissionInfo(
                missionName = missionName,
                userId = userId,
                totalStamp = missionCount.value!!,
                startDate = missionStartDate.value!!,
                dueDate = missionEndDate.value!!
            )
        )
    }

    fun showNumberPicker() {
        _numberCountClick.postValue(Event(Unit))
    }

    fun updateMissionCount(count: Int) {
        _missionCount.value = count
    }

    fun updateMissionStartDate(year: Int, month: Int, date: Int) {
        _missionStartDate.value = getMissionIntFormatDate(year, month, date)
        LogUtil.log("update", _missionStartDate.value.toString())
    }

    fun updateMissionEndDate(year: Int, month: Int, date: Int) {
        _missionEndDate.value = getMissionIntFormatDate(year, month, date)
    }

    fun checkMissionValid() {
        _checkMissionValidEvent.postValue(Event(Unit))
    }
}
