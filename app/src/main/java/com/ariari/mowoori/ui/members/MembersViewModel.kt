package com.ariari.mowoori.ui.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MembersRepository
import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val membersRepository: MembersRepository,
) : ViewModel() {
    private val _openInviteDialogEvent = MutableLiveData<Event<String?>>()
    val openInviteDialogEvent: LiveData<Event<String?>> = _openInviteDialogEvent

    private val _currentGroup = MutableLiveData<Group>()
    val currentGroup: LiveData<Group> = _currentGroup

    fun fetchGroupInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            membersRepository.getCurrentGroupInfo().onSuccess {
                _currentGroup.postValue(it)
            }
        }
    }

    fun clickPlusButton() {
        _openInviteDialogEvent.value = Event(currentGroup.value?.groupId)
    }
}
