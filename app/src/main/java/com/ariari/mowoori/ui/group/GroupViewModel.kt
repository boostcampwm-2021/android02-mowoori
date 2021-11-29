package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.util.DuplicatedException
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

    private val _isNetworkDialogShowed = MutableLiveData(false)
    val isNetworkDialogShowed: LiveData<Boolean> get() = _isNetworkDialogShowed

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = false
    }

    fun setGroupName() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val nickname = introRepository.getRandomNickName().getOrThrow()
            groupName.postValue(nickname + "들")
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    fun joinGroup() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val code = groupName.value ?: ""
            if (!checkInviteCodeValidation(code)) return@launch
            groupRepository.hasExistGroupId(code).getOrThrow()
            val user = groupRepository.getUser().getOrThrow()
            val isSuccess = groupRepository.addUserToGroup(code, user).getOrThrow()
            _successAddGroup.postValue(isSuccess)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
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

    fun addNewGroup() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val name = groupName.value ?: ""
            if (!checkGroupNameValidation(name)) return@launch
            val user = groupRepository.getUser().getOrThrow()
            val groupNameList = groupRepository.getGroupNameList().getOrThrow()
            val groupInfo = GroupInfo(0, name, listOf(user.userId))
            val isSuccess =
                groupRepository.putGroupInfo(groupNameList, groupInfo, user).getOrThrow()
            _successAddGroup.postValue(isSuccess)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        } catch (e: DuplicatedException) {
            _inValidMode.postValue(InvalidMode.AlreadyExistGroupName)
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

    private fun checkNetworkDialog() {
        _isNetworkDialogShowed.value?.let {
            if (!it) _isNetworkDialogShowed.postValue(true)
        }
    }
}
