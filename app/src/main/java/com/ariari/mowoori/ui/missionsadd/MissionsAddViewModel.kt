package com.ariari.mowoori.ui.missionsadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
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
    private val missionsRepository: MissionsRepository,
) : ViewModel() {
    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> = _backBtnClick

    private val _numberCountClick = MutableLiveData<Event<Unit>>()
    val numberCountClick: LiveData<Event<Unit>> = _numberCountClick

    private val _missionCount = MutableLiveData<Int>(10)
    val missionCount: LiveData<Int> = _missionCount

    private val _missionStartDate = MutableLiveData(getCurrentDate())
    val missionStartDate: LiveData<Int> = _missionStartDate

    private val _missionEndDate = MutableLiveData(getCurrentDatePlusMonths(1))
    val missionEndDate: LiveData<Int> = _missionEndDate

    private val _checkMissionValidEvent = MutableLiveData<Event<Unit>>()
    val checkMissionValidEvent: LiveData<Event<Unit>> = _checkMissionValidEvent

    private val _isMissionPosted = MutableLiveData<Boolean>()
    val isMissionPosted: LiveData<Boolean> = _isMissionPosted

    private val _isNetworkDialogShowed = MutableLiveData(Event(false))
    val isNetworkDialogShowed: LiveData<Event<Boolean>> get() = _isNetworkDialogShowed

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = Event(false)
    }

    init {
        _missionStartDate.value = getCurrentDate()
        _missionEndDate.value = getCurrentDatePlusMonths(1)
        updateMissionCount(10)
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    fun postMission(missionName: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val user = missionsRepository.getUser().getOrThrow()
            val missionInfo = getMissionInfo(user.userId, missionName)
            val missionIdList =
                missionsRepository.getMissionIdList(user.userInfo.currentGroupId).getOrThrow()
            val isSuccess = missionsRepository.postMission(missionInfo,
                user.userInfo.currentGroupId,
                missionIdList).getOrThrow()
            _isMissionPosted.postValue(isSuccess)
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        } catch (e: Exception) {
            checkNetworkDialog()
        }
    }

    private fun getMissionInfo(userId: String, missionName: String): MissionInfo {
        return MissionInfo(
            missionName = missionName,
            userId = userId,
            totalStamp = missionCount.value!!,
            startDate = missionStartDate.value!!,
            dueDate = missionEndDate.value!!
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

    private fun checkNetworkDialog() {
        _isNetworkDialogShowed.value?.let {
            if (!it.peekContent()) {
                _isNetworkDialogShowed.postValue(Event(true))
            }
        }
    }
}
