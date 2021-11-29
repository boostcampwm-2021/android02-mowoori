package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.util.InvalidMode
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val introRepository: IntroRepository,
) : ViewModel() {

    val groupName = MutableLiveData("")

    private val _addGroupCompleteEvent = MutableLiveData<Event<String>>()
    val addGroupCompleteEvent: LiveData<Event<String>> = _addGroupCompleteEvent

    private val _inValidMode = MutableLiveData<InvalidMode>()
    val inValidMode: LiveData<InvalidMode> = _inValidMode

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
        val code = groupName.value ?: ""
        if (!checkInviteCodeValidation(code)) {
            _inValidMode.postValue(InvalidMode.InValidCode)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.isExistGroupId(code)
                .onSuccess {
                    if (it) {
                        initRequestCount()
                        groupRepository.getUser()
                            .onSuccess { user ->
                                initRequestCount()
                                groupRepository.addUserToGroup(code, user)
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
                        _inValidMode.postValue(InvalidMode.InValidCode)
                    }
                }
                .onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    fun addNewGroup() {
        val name = groupName.value ?: ""
        if (!checkGroupNameValidation(name)) {
            _inValidMode.postValue(InvalidMode.InValidGroupName)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            groupRepository.getUser()
                .onSuccess { user ->
                    checkGroupNameExist(name, user)
                }
                .onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    private suspend fun checkGroupNameExist(name: String, user: User) {
        initRequestCount()
        groupRepository.getGroupNameList()
            .onSuccess { groupNameList ->
                val groupInfo = GroupInfo(0, name, listOf(user.userId))
                initRequestCount()
                groupRepository.putGroupInfo(groupNameList, groupInfo, user)
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

    private fun checkGroupNameValidation(groupName: String): Boolean {
        return groupName.length <= 11 && groupName.isNotEmpty()
    }

    private fun checkInviteCodeValidation(code: String): Boolean {
        return code.isNotEmpty()
    }

    private fun checkThrowableMessage(throwable: Throwable) {
        when (throwable.message) {
            ErrorMessage.Offline.message -> {
                addRequestCount()
                checkRequestCount()
            }
            ErrorMessage.GroupInfo.message -> {
                _inValidMode.postValue(InvalidMode.InValidCode)
            }
            ErrorMessage.DuplicatedGroup.message -> {
                _inValidMode.postValue(InvalidMode.AlreadyJoin)
            }
            ErrorMessage.ExistGroupName.message -> {
                _inValidMode.postValue(InvalidMode.AlreadyExistGroupName)
            }
            else -> Unit
        }
    }

    private fun setNetworkDialogEvent() {
        _networkDialogEvent.postValue(true)
    }
}
