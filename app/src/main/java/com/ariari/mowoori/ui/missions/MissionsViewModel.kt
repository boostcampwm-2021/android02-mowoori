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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MissionsViewModel @Inject constructor(
    private val missionsRepository: MissionsRepository,
) : ViewModel() {
    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent

    private val _plusBtnClick = MutableLiveData<Event<Boolean>>()
    val plusBtnClick: LiveData<Event<Boolean>> = _plusBtnClick

    private val _itemClick = MutableLiveData<Event<Mission>>()
    val itemClick: LiveData<Event<Mission>> = _itemClick

    private val _missionsType = MutableLiveData(NOT_DONE_TYPE)
    val missionsType: LiveData<Int> = _missionsType

    private val _missionsList = MutableLiveData<Event<List<Mission>>>()
    val missionsList: LiveData<Event<List<Mission>>> = _missionsList

    private val _user = MutableLiveData<Event<User>>()
    val user: LiveData<Event<User>> = _user

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _networkDialogEvent = MutableLiveData<Event<Boolean>>()
    val networkDialogEvent: LiveData<Event<Boolean>> get() = _networkDialogEvent

    fun setLoadingEvent(isLoading: Boolean) {
        _loadingEvent.value = Event(isLoading)
    }

    fun setPlusBtnClick() {
        _plusBtnClick.value = Event(true)
    }

    fun setItemClick(mission: Mission) {
        _itemClick.postValue(Event(mission))
    }

    fun setNotDoneType() {
        _missionsType.value = NOT_DONE_TYPE
        setLoadingEvent(true)
    }

    fun setDoneType() {
        _missionsType.value = DONE_TYPE
        setLoadingEvent(true)
    }

    fun setFailType() {
        _missionsType.value = FAIL_TYPE
        setLoadingEvent(true)
    }

    fun sendUserToLoadMissions(user: User?) {
        if (user != null) {
            loadUser(user)
            loadMissionIdList(user)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                missionsRepository.getUser().onSuccess { user ->
                    loadUser(user)
                    loadMissionIdList(user)
                }.onFailure { exception ->
                  Timber.e(exception)
                  setNetworkDialogEvent() 
                  _errorMessage.postValue(Event("getUser")
                }
            }
        }
    }

    private fun loadMissionIdList(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            missionsRepository.getMissionIdList(user.userInfo.currentGroupId)
                .onSuccess { missionIdList ->
                    loadMissionList(user.userId, missionIdList)
                }
                .onFailure { exception ->
                    Timber.e(exception)
                    _errorMessage.postValue(Event("loadMissionList"))
                    setNetworkDialogEvent()
                }
        }
    }

    private suspend fun loadMissionList(userId: String, missionIdList: List<String>) {
        missionsRepository.getMissions(userId)
            .onSuccess { missionList ->
                _missionsList.postValue(
                    when (requireNotNull(missionsType.value).peekContent()) {
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
                    })
            }
            .onFailure {
                setNetworkDialogEvent()
            }
    }
    
    private fun loadUser(user: User) {
        _user.postValue(Event(user))
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)
        _networkDialogEvent.postValue(Event(true))
    }

    companion object {
        const val NOT_DONE_TYPE = 0
        const val DONE_TYPE = 1
        const val FAIL_TYPE = 2
    }
}
