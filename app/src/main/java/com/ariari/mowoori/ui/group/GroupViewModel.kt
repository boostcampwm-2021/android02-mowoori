package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val introRepository: IntroRepository,
) : ViewModel() {

    val groupName = MutableLiveData("")

    private val _addGroupCompleteEvent = MutableLiveData<Event<String>>()
    val addGroupCompleteEvent: LiveData<Event<String>> = _addGroupCompleteEvent

    private val _inValidEvent = MutableLiveData<Event<Unit>>()
    val inValidEvent: LiveData<Event<Unit>> = _inValidEvent

    private val _networkDialogEvent = MutableLiveData<Boolean>()
    val networkDialogEvent: LiveData<Boolean> get() = _networkDialogEvent

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

    fun setGroupName() {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            introRepository.getRandomNickName()
                .onSuccess { randomName -> groupName.postValue(randomName + "들") }
                .onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    fun joinGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = groupName.value ?: return@launch
            groupRepository.isExistGroupId(name)
                .onSuccess {
                    if (it) {
                        initRequestCount()
                        groupRepository.getUser()
                            .onSuccess { user ->
                                initRequestCount()
                                groupRepository.addUserToGroup(name, user)
                                    .onSuccess { newGroupId ->
                                        _addGroupCompleteEvent.postValue(Event(newGroupId))
                                    }
                                    .onFailure { throwable ->
                                        checkThrowableMessage(throwable)
                                    }
                            }.onFailure { throwable ->
                                checkThrowableMessage(throwable)
                            }
                    } else {
                        _inValidEvent.postValue(Event(Unit))
                    }
                }
                .onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    fun addNewGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            groupRepository.getUser()
                .onSuccess {
                    val name = groupName.value ?: return@launch
                    val groupInfo = GroupInfo(0, name, listOf(it.userId))
                    initRequestCount()
                    groupRepository.putGroupInfo(groupInfo, it)
                        .onSuccess { newGroupId ->
                            _addGroupCompleteEvent.postValue(Event(newGroupId))
                        }
                        .onFailure { throwable ->
                            checkThrowableMessage(throwable)
                        }
                }
                .onFailure {
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
            ErrorMessage.GroupInfo.message -> {
                // TODO: 잘못된 초대 코드 핸들링
                Timber.e(throwable)
            }
            else -> Unit
        }
    }

    private fun setNetworkDialogEvent() {
        _networkDialogEvent.postValue(true)
    }
}
