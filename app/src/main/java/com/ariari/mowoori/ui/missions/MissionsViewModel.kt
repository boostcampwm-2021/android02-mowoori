package com.ariari.mowoori.ui.missions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MissionsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionsRepository: MissionsRepository,
) : ViewModel() {
    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent

    private val _plusBtnClick = MutableLiveData<Event<Boolean>>()
    val plusBtnClick: LiveData<Event<Boolean>> = _plusBtnClick

    private val _backBtnClick = MutableLiveData<Event<Boolean>>()
    val backBtnClick: LiveData<Event<Boolean>> get() = _backBtnClick

    private val _isMemberMission = MutableLiveData<Boolean>()
    val isMemberMission: LiveData<Boolean> = _isMemberMission

    var isEmptyGroupList = false
        private set

    private val _itemClick = MutableLiveData<Event<Mission>>()
    val itemClick: LiveData<Event<Mission>> = _itemClick

    private val _missionsType = MutableLiveData(NOT_DONE_TYPE)
    val missionsType: LiveData<Int> = _missionsType

    //    private val _missionIdList = MutableLiveData<List<String>>(emptyList())
//    private val _missionList = MutableLiveData<List<Mission>>(emptyList())
    private val _filteredMissionList = MutableLiveData<List<Mission>>(emptyList())
    val filteredMissionList: LiveData<List<Mission>> = _filteredMissionList

    private val _user = MutableLiveData<Event<User>>()
    val user: LiveData<Event<User>> = _user

    private val _isNetworkDialogShowed = MutableLiveData(Event(false))
    val isNetworkDialogShowed: LiveData<Event<Boolean>> get() = _isNetworkDialogShowed

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = Event(false)
    }

    fun setLoadingEvent(isLoading: Boolean) {
        _loadingEvent.postValue(Event(isLoading))
    }

    fun setPlusBtnClick() {
        _plusBtnClick.value = Event(true)
    }

    fun setBackBtnClick() {
        _backBtnClick.value = Event(true)
    }

    fun setItemClick(mission: Mission) {
        _itemClick.postValue(Event(mission))
    }

    fun setNotDoneType() {
        _missionsType.value = NOT_DONE_TYPE
//        filterMissionList(requireNotNull(_missionIdList.value), requireNotNull(_missionList.value))
        setLoadingEvent(true)
    }

    fun setDoneType() {
        _missionsType.value = DONE_TYPE
//        filterMissionList(requireNotNull(_missionIdList.value), requireNotNull(_missionList.value))
        setLoadingEvent(true)
    }

    fun setFailType() {
        _missionsType.value = FAIL_TYPE
//        filterMissionList(requireNotNull(_missionIdList.value), requireNotNull(_missionList.value))
        setLoadingEvent(true)
    }

    fun setIsMemberMission(user: User?) {
        if (user != null) _isMemberMission.value = true
    }

    fun loadUserThenLoadMissionList(user: User?) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val missionUser = user ?: missionsRepository.getUser().getOrThrow()
            _user.postValue(Event(missionUser))
            isEmptyGroupList = missionUser.userInfo.groupList.isEmpty()
            val missionIdList =
                missionsRepository.getMissionIdList(missionUser.userInfo.currentGroupId)
                    .getOrThrow()
            val missionList = missionsRepository.getMissions(missionUser.userId).getOrThrow()
//            _missionIdList.postValue(missionIdList)
//            _missionList.postValue(missionList)
            filterMissionList(missionIdList, missionList)
            setLoadingEvent(false)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    private fun filterMissionList(missionIdList: List<String>, missionList: List<Mission>) {
        val filteredList = when (requireNotNull(missionsType.value)) {
            NOT_DONE_TYPE -> {
                missionList.filter {
                    (missionIdList.contains(it.missionId)) &&
                            (getCurrentDate() <= it.missionInfo.dueDate) &&
                            (it.missionInfo.curStamp < it.missionInfo.totalStamp)
                }
            }
            DONE_TYPE -> {
                missionList.filter {
                    (missionIdList.contains(it.missionId)) &&
                            (it.missionInfo.curStamp == it.missionInfo.totalStamp)
                }
            }
            FAIL_TYPE -> {
                missionList.filter {
                    (missionIdList.contains(it.missionId)) &&
                            (getCurrentDate() > it.missionInfo.dueDate) &&
                            (it.missionInfo.curStamp < it.missionInfo.totalStamp)
                }
            }
            else -> emptyList()
        }
        _filteredMissionList.postValue(filteredList)

    }

    private fun checkNetworkDialog() {
        setLoadingEvent(false)
        _isNetworkDialogShowed.value?.let {
            if (!it.peekContent()) {
                _isNetworkDialogShowed.postValue(Event(true))
            }
        }
    }

    companion object {
        const val NOT_DONE_TYPE = 0
        const val DONE_TYPE = 1
        const val FAIL_TYPE = 2
    }
}
