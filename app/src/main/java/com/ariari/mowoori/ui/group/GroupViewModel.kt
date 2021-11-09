package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.GroupRepository
import com.ariari.mowoori.data.repository.IntroRepository
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val introRepository: IntroRepository
) : ViewModel() {

    val groupName = MutableLiveData<String>("")

    private val _addGroupCompleteEvent = MutableLiveData<Event<String>>()
    val addGroupCompleteEvent: LiveData<Event<String>> = _addGroupCompleteEvent

    private val _inValidEvent = MutableLiveData<Event<Unit>>()
    val inValidEvent: LiveData<Event<Unit>> = _inValidEvent

    fun setGroupName() {
        viewModelScope.launch(Dispatchers.IO) {
            val randomName = introRepository.getRandomNickName()
            groupName.postValue(randomName + "들")
        }
    }

    fun joinGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = groupName.value ?: return@launch
            val exist = groupRepository.isExistGroupId(name)
            if (!exist) {
                _inValidEvent.postValue(Event(Unit))
            } else {
                val user = groupRepository.getUser().getOrNull() ?: run {
                    _addGroupCompleteEvent.postValue(Event(""))
                    return@launch
                }
                groupRepository.addUserToGroup(name, user).onSuccess { newGroupId ->
                    _addGroupCompleteEvent.postValue(Event(newGroupId))
                }.onFailure {
                    _addGroupCompleteEvent.postValue(Event(""))
                }
            }
        }
    }

    fun addNewGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.getUser().onSuccess {
                val name = groupName.value ?: return@launch
                val groupInfo = GroupInfo(name, listOf(it.userId))
                groupRepository.putGroupInfo(groupInfo, it).onSuccess { newGroupId ->
                    _addGroupCompleteEvent.postValue(Event(newGroupId))
                }.onFailure {
                    _addGroupCompleteEvent.postValue(Event(""))
                }
            }
        }
    }
}
