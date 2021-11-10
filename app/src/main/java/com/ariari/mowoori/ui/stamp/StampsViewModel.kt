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

    private val _stampIdList = MutableLiveData<List<String>>()
    val stampIdList: LiveData<List<String>> get() = _stampIdList

    private val _stampList = MutableLiveData<MutableList<Stamp>>()
    val stampList: LiveData<MutableList<Stamp>> get() = _stampList

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
        stampIdList.forEachIndexed { index, stampId ->
            viewModelScope.launch(Dispatchers.IO) {
                stampsRepository.getStampInfo(stampId)
                    .onSuccess { stampInfo ->
                        putStamp(stampId, index, stampInfo)
                    }
                    .onFailure {
                        println("Stamp - ${it.message}")
                    }
            }
        }
    }

    private fun putStamp(stampId: String, index: Int, stampInfo: StampInfo) {
        val tempStampList = _stampList.value ?: return
        tempStampList[index] = Stamp(stampId, stampInfo)
        _stampList.postValue(tempStampList)
    }
}
