package com.ariari.mowoori.ui.stamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StampsViewModel @Inject constructor(private val stampsRepository: StampsRepository) :
    ViewModel() {

    private val _spanCount = MutableLiveData<Event<Int>>()
    val spanCount: LiveData<Event<Int>> get() = _spanCount

    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> get() = _backBtnClick

    private val _missionName = MutableLiveData<Event<String>>()
    val missionName: LiveData<Event<String>> get() = _missionName

    private val _stampList = MutableLiveData<MutableList<Stamp>>()
    val stampList: LiveData<MutableList<Stamp>> get() = _stampList

    private val _selectedStampInfo = MutableLiveData<Event<StampInfo>>()
    val selectedStampInfo: LiveData<Event<StampInfo>> get() = _selectedStampInfo

    private val _isMyMission = MutableLiveData<Event<Boolean>>()
    val isMyMission: LiveData<Event<Boolean>> get() = _isMyMission

    fun setSpanCount(result: Float) {
        _spanCount.value = Event(result.toInt())
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    fun setMissionName(title: String) {
        _missionName.value = Event(title)
    }

    fun setAllEmptyStamps(totalStamp: Int) {
        val tempEmptyStampList = mutableListOf<Stamp>()
        repeat(totalStamp) {
            tempEmptyStampList.add(Stamp(stampInfo = StampInfo()))
        }
        _stampList.value = tempEmptyStampList
    }

    fun setStampList(stampIdList: List<String>) {
        val currentStampList = _stampList.value ?: return
        // postValue 이슈 방지를 위해 for 문 밖에서 스코프 설정
        viewModelScope.launch(Dispatchers.IO) {
            val tempStampList = mutableListOf<Stamp>()
            stampIdList.forEach { stampId ->
                stampsRepository.getStampInfo(stampId)
                    .onSuccess { stampInfo ->
                        tempStampList.add(Stamp(stampId, stampInfo))
                    }
                    .onFailure {
                        println("${it.message}")
                    }
            }
            // 리스트의 깊은 복사를 위한 addAll 처리
            tempStampList.addAll(currentStampList.subList(stampIdList.size, currentStampList.size))
            _stampList.postValue(tempStampList)
        }
    }

    fun setSelectedStampInfo(position: Int, currentStamp: Int) {
        if (position >= currentStamp) return
        _selectedStampInfo.value = Event(_stampList.value?.get(position)?.stampInfo!!)
    }

    fun setIsMyMission(userId: String) {
        stampsRepository.getUserId()
            .onSuccess { uid ->
                _isMyMission.value = Event(uid == userId)
            }
            .onFailure {
                println("${it.message}")
            }
    }
}
