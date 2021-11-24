package com.ariari.mowoori.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StampsViewModel @Inject constructor(
    private val stampsRepository: StampsRepository,
    private val missionsRepository: MissionsRepository,
) : ViewModel() {

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> get() = _loadingEvent

    private val _spanCount = MutableLiveData<Event<Int>>()
    val spanCount: LiveData<Event<Int>> get() = _spanCount

    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> get() = _backBtnClick

    private val _mission = MutableLiveData<Mission>()
    val mission: LiveData<Mission> get() = _mission

    val stampList: LiveData<Event<MutableList<Stamp>>> =
        mission.switchMap { getAllStamps(it.missionInfo) }

    private val _isMyMission = MutableLiveData<Event<Boolean>>()
    val isMyMission: LiveData<Event<Boolean>> get() = _isMyMission

    private val _networkDialogEvent = MutableLiveData<Event<Boolean>>()
    val networkDialogEvent: LiveData<Event<Boolean>> get() = _networkDialogEvent

    private var _requestCount = 0
    private val requestCount get() = _requestCount

    private fun initRequestCount() {
        _requestCount = 0
    }

    private fun addRequestCount() {
        _requestCount++
    }

    private fun checkRequestCount() {
        if (requestCount == 1) {
            setNetworkDialogEvent()
        }
    }

    fun setLoadingEvent(flag: Boolean) {
        _loadingEvent.postValue(Event(flag))
    }

    fun setSpanCount(result: Float) {
        _spanCount.postValue(Event(result.toInt()))
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    private fun getAllStamps(missionInfo: MissionInfo): LiveData<Event<MutableList<Stamp>>> {
        val tempMutableLiveData = MutableLiveData<Event<MutableList<Stamp>>>()
        viewModelScope.launch(Dispatchers.IO) {
            val tempStampIdList = mutableListOf<String>()
            val deferredStampList = missionInfo.stampList.map { stampId ->
                tempStampIdList.add(stampId)
                async { stampsRepository.getStampInfo(stampId) }
            }
            val tempStampList = deferredStampList.awaitAll().mapIndexed { index, result ->
                initRequestCount()
                if (result.isSuccess) {
                    val stampInfo = result.getOrNull() ?: return@launch
                    Stamp(tempStampIdList[index], stampInfo)
                } else {
                    val throwable = result.exceptionOrNull() ?: return@launch
                    checkThrowableMessage(throwable)
                    return@launch
                }
            }.toMutableList()
            tempStampList.addAll(createEmptyStamps(missionInfo.totalStamp - tempStampList.size))
            tempMutableLiveData.postValue(Event(tempStampList))
        }
        return tempMutableLiveData
    }

    private fun createEmptyStamps(count: Int): MutableList<Stamp> {
        val tempStampList = mutableListOf<Stamp>()
        repeat(count) { tempStampList.add(Stamp(stampInfo = StampInfo(pictureUrl = "empty"))) }
        return tempStampList
    }

    fun setIsMyMission(userId: String) {
        stampsRepository.getUserId()
            .onSuccess { uid ->
                _isMyMission.value = Event(uid == userId)
            }
            .onFailure {
                Timber.e("$it")
            }
    }

    fun loadMissionInfo(missionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            missionsRepository.getMissionInfo(missionId)
                .onSuccess {
                    _mission.postValue(Mission(missionId, it))
                }.onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    private fun checkThrowableMessage(throwable: Throwable) {
        when (throwable.message) {
            ErrorMessage.Offline.message -> {
                addRequestCount()
                checkRequestCount()
            }
            ErrorMessage.MissionInfo.message -> {
            }
            ErrorMessage.StampInfo.message -> {
            }
            else -> Unit
        }
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)

        _networkDialogEvent.postValue(Event(true))
    }

}
