package com.ariari.mowoori.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
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

    private val _mission = MutableLiveData<Event<Mission>>()
    val mission: LiveData<Event<Mission>> get() = _mission

    private val _stampList = MutableLiveData<Event<MutableList<Stamp>>>()
    val stampList: LiveData<Event<MutableList<Stamp>>> = _stampList

    private val _isMyMission = MutableLiveData<Event<Boolean>>()
    val isMyMission: LiveData<Event<Boolean>> get() = _isMyMission

    private val _isNetworkDialogShowed = MutableLiveData(Event(false))
    val isNetworkDialogShowed: LiveData<Event<Boolean>> get() = _isNetworkDialogShowed

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = Event(false)
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

    fun setIsMyMission(userId: String) {
        try {
            val uid = stampsRepository.getUserId().getOrThrow()
            _isMyMission.value = Event(userId == uid)
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    fun loadMissionInfo(missionId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val missionInfo = missionsRepository.getMissionInfo(missionId).getOrThrow()
            _mission.postValue(Event(Mission(missionId, missionInfo)))
            val deferredStampList = missionInfo.stampList.map { stampId ->
                async { stampsRepository.getStampInfo(stampId) }
            }
            val tempStampList = deferredStampList.awaitAll().mapIndexed { index, result ->
                try {
                    val stampInfo = result.getOrThrow()
                    Stamp(missionInfo.stampList[index], stampInfo)
                } catch (e: Exception) {
                    checkNetworkDialog()
                    return@launch
                } catch (e: NullPointerException) {
                    return@launch
                }
            }.toMutableList()
            tempStampList.addAll(createEmptyStamps(missionInfo.totalStamp - tempStampList.size))
            _stampList.postValue(Event(tempStampList))
            setLoadingEvent(false)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    private fun createEmptyStamps(count: Int): MutableList<Stamp> {
        val tempEmptyStampList = mutableListOf<Stamp>()
        repeat(count) { tempEmptyStampList.add(Stamp(stampInfo = StampInfo(pictureUrl = "empty"))) }
        return tempEmptyStampList
    }

    private fun checkNetworkDialog() {
        setLoadingEvent(false)
        _isNetworkDialogShowed.value?.let {
            if (!it.peekContent()) _isNetworkDialogShowed.postValue(Event(true))
        }
    }
}
