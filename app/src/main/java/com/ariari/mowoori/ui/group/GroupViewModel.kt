package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.InvalidMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val introRepository: IntroRepository,
) : ViewModel() {

    // 그룹 이름, 초대 코드랑 같이 쓰여서 수정 필요
    val groupName = MutableLiveData("")

    private val _successAddGroup = MutableLiveData<Boolean>()
    val successAddGroup: LiveData<Boolean> = _successAddGroup

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

    fun setGroupName() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val nickname = getNickName()
            groupName.postValue(nickname + "들")
        } catch (throwable: Throwable) {
            checkThrowableMessage(throwable)
        }
    }

    private suspend fun getNickName(): String {
        initRequestCount()
        return introRepository.getRandomNickName().getOrThrow()
    }

    fun joinGroup() {
        val code = groupName.value ?: ""
        if (!checkInviteCodeValidation(code)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                checkGroupId(code) // 실패시 throw
                val user = getUser()
                val isSuccess = addUserToGroup(code, user)
                _successAddGroup.postValue(isSuccess)
            } catch (throwable: Throwable) {
                checkThrowableMessage(throwable)
            }
        }
    }

    private fun checkInviteCodeValidation(code: String): Boolean {
        return if (code.isNotEmpty()) {
            true
        } else {
            _inValidMode.postValue(InvalidMode.InValidCode)
            false
        }
    }

    private suspend fun checkGroupId(code: String): Boolean {
        initRequestCount()
        return groupRepository.hasExistGroupId(code).getOrThrow()
    }

    private suspend fun addUserToGroup(code: String, user: User): Boolean {
        initRequestCount()
        return groupRepository.addUserToGroup(code, user).getOrThrow()
    }

    fun addNewGroup() {
        val name = groupName.value ?: ""
        if (!checkGroupNameValidation(name)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = getUser()
                val groupNameList = getGroupNameList()
                val groupInfo = GroupInfo(0, name, listOf(user.userId))
                val isSuccess = putGroupInfo(groupNameList, groupInfo, user)
                _successAddGroup.postValue(isSuccess)
            } catch (throwable: Throwable) {
                checkThrowableMessage(throwable)
            }
        }
    }

    private fun checkGroupNameValidation(groupName: String): Boolean {
        return if (groupName.length <= 11 && groupName.isNotEmpty()) {
            true
        } else {
            _inValidMode.postValue(InvalidMode.InValidGroupName)
            false
        }
    }

    private suspend fun getUser(): User {
        initRequestCount()
        return groupRepository.getUser().getOrThrow()
    }

    private suspend fun getGroupNameList(): List<String> {
        initRequestCount()
        return groupRepository.getGroupNameList().getOrThrow()
    }

    private suspend fun putGroupInfo(
        groupNameList: List<String>,
        groupInfo: GroupInfo,
        user: User,
    ): Boolean {
        initRequestCount()
        return groupRepository.putGroupInfo(groupNameList, groupInfo, user).getOrThrow()
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
