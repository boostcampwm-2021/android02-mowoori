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
    private val introRepository: IntroRepository,
) : ViewModel() {

    val groupName = MutableLiveData("")

    private val _addGroupCompleteEvent = MutableLiveData<Event<String>>()
    val addGroupCompleteEvent: LiveData<Event<String>> = _addGroupCompleteEvent

    private val _inValidEvent = MutableLiveData<Event<Unit>>()
    val inValidEvent: LiveData<Event<Unit>> = _inValidEvent

    private val _networkDialogEvent = MutableLiveData<Boolean>()
    val networkDialogEvent: LiveData<Boolean> get() = _networkDialogEvent

    fun setGroupName() {
        viewModelScope.launch(Dispatchers.IO) {
            introRepository.getRandomNickName()
                .onSuccess { randomName -> groupName.postValue(randomName + "들") }
                .onFailure { setNetworkDialogEvent() }
        }
    }

    fun joinGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = groupName.value ?: return@launch
            val exist = groupRepository.isExistGroupId(name)
            if (!exist) {
                _inValidEvent.postValue(Event(Unit))
            } else {
                groupRepository.getUser()
                    .onSuccess {
                        groupRepository.addUserToGroup(name, it)
                            .onSuccess { newGroupId ->
                                _addGroupCompleteEvent.postValue(Event(newGroupId))
                            }
                            .onFailure {
                                _addGroupCompleteEvent.postValue(Event(""))
                                setNetworkDialogEvent()
                            }
                    }.onFailure {
                        _addGroupCompleteEvent.postValue(Event(""))
                        setNetworkDialogEvent()
                    }
            }
        }
    }

    fun addNewGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.getUser()
                .onSuccess {
                    val name = groupName.value ?: return@launch
                    val groupInfo = GroupInfo(0, name, listOf(it.userId))
                    groupRepository.putGroupInfo(groupInfo, it)
                        .onSuccess { newGroupId ->
                            _addGroupCompleteEvent.postValue(Event(newGroupId))
                        }
                        .onFailure {
                            _addGroupCompleteEvent.postValue(Event(""))
                            setNetworkDialogEvent()
                        }
                }
                .onFailure {
                    setNetworkDialogEvent()
                }
        }
    }

    private fun setNetworkDialogEvent() {
        _networkDialogEvent.postValue(true)
    }
}
