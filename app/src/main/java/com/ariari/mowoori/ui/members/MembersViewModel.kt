package com.ariari.mowoori.ui.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.MembersRepository
import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.register.entity.User
import com.ariari.mowoori.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val membersRepository: MembersRepository,
) : ViewModel() {

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> get() = _loadingEvent

    private val _openInviteDialogEvent = MutableLiveData<Event<String?>>()
    val openInviteDialogEvent: LiveData<Event<String?>> = _openInviteDialogEvent

    private val _currentGroup = MutableLiveData<Group>()
    val currentGroup: LiveData<Group> = _currentGroup

    private val _membersList = MutableLiveData<List<User>>()
    val membersList: LiveData<List<User>> = _membersList

    fun setLoadingEvent(isLoading: Boolean) {
        _loadingEvent.value = Event(isLoading)
    }

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

    fun fetchMemberList() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredMemberList =
                requireNotNull(currentGroup.value).groupInfo.userList.map { userId ->
                    async { membersRepository.getUserInfo(userId) }
                }
            _membersList.postValue(
                deferredMemberList.awaitAll().map { result -> result ?: return@launch })
        }
    }
}
