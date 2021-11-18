package com.ariari.mowoori.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StampsViewModel @Inject constructor(
    private val stampsRepository: StampsRepository,
    private val missionsRepository: MissionsRepository
) : ViewModel() {

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> get() = _loadingEvent

    private val _spanCount = MutableLiveData<Event<Int>>()
    val spanCount: LiveData<Event<Int>> get() = _spanCount

    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> get() = _backBtnClick

    private val _mission = MutableLiveData<Mission>()
    val mission: LiveData<Mission> get() = _mission

    private val _stampList = MutableLiveData<MutableList<Stamp>>()
    val stampList: LiveData<MutableList<Stamp>> get() = _stampList

    private val _curStampList = MutableLiveData<MutableList<Stamp>>()
    val curStampList: LiveData<MutableList<Stamp>> get() = _curStampList

    private val _selectedStampInfo = MutableLiveData<Event<StampInfo>>()
    val selectedStampInfo: LiveData<Event<StampInfo>> get() = _selectedStampInfo

    private val _isMyMission = MutableLiveData<Event<Boolean>>()
    val isMyMission: LiveData<Event<Boolean>> get() = _isMyMission

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun setLoadingEvent(flag: Boolean) {
        _loadingEvent.postValue(Event(flag))
    }

    fun setSpanCount(result: Float) {
        _spanCount.postValue(Event(result.toInt()))
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

//    fun setAllEmptyStamps(totalStamp: Int) {
//        val tempEmptyStampList = mutableListOf<Stamp>()
//        repeat(totalStamp) {
//            tempEmptyStampList.add(Stamp(stampInfo = StampInfo()))
//        }
//        _stampList.value = tempEmptyStampList
//    }

    fun setStampList() {
        // postValue 이슈 방지를 위해 for 문 밖에서 스코프 설정
        viewModelScope.launch(Dispatchers.IO) {
            val tempStampList = mutableListOf<Stamp>()
            mission.value?.missionInfo?.stampList?.forEach { stampId ->
                stampsRepository.getStampInfo(stampId)
                    .onSuccess { stampInfo ->
                        tempStampList.add(Stamp(stampId, stampInfo))
                    }
                    .onFailure {
                        Timber.e("stampInfo Error: $it")
                    }
            }
            _curStampList.postValue(tempStampList)
            setLoadingEvent(false)
        }
    }

    fun fillEmptyStamps(count: Int) {
        if (count == 0) return

        curStampList.value?.let {
            val tempEmptyStampList = it.toMutableList()
            repeat(count) {
                tempEmptyStampList.add(Stamp(stampInfo = StampInfo()))
            }
            _stampList.value = tempEmptyStampList
        }
    }

    fun setSelectedStampInfo(position: Int, currentStamp: Int) {
        if (position >= currentStamp) return
        _selectedStampInfo.value = Event(_stampList.value?.get(position)?.stampInfo!!)
    }

    fun setIsMyMission() {
        LogUtil.log("setIsMyMission", user.value?.userId.toString())
        stampsRepository.getUserId()
            .onSuccess { uid ->
                _isMyMission.value = Event(uid == user.value!!.userId)
            }
            .onFailure {
                Timber.e("${it.message}")
            }
    }

    fun loadMissionInfo(missionId: String) {
        viewModelScope.launch {
            LogUtil.log("missionId - loadMission", missionId)
            missionsRepository.getMissionInfo(missionId).onSuccess {
                _mission.postValue(Mission(missionId, it))
            }.onFailure {
                Timber.e("$it")
            }
        }
    }

    fun setUser(user: User) {
        _user.postValue(user)
        LogUtil.log("setUser", user.toString())
    }
}
