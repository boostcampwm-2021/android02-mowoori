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
import timber.log.Timber
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

    private val _networkDialogEvent = MutableLiveData<Event<Boolean>>()
    val networkDialogEvent: LiveData<Event<Boolean>> get() = _networkDialogEvent

    private var _requestCount = 0
    private val requestCount get() = _requestCount

    private fun initRequestCount() {
        _requestCount = 0
    }

    private fun addRequestCount() {
        _requestCount++
    }

    private fun checkRequestCount() {
        if (requestCount > 1) {
            setNetworkDialogEvent()
        }
    }

    fun setLoadingEvent(isLoading: Boolean) {
        _loadingEvent.postValue(Event(isLoading))
    }

    fun fetchGroupInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            initRequestCount()
            membersRepository.getCurrentGroupInfo()
                .onSuccess {
                    _currentGroup.postValue(it)
                }
                .onFailure {
                    addRequestCount()
                    checkRequestCount()
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
                    async {
                        membersRepository.getUserInfo(userId)
                            .onFailure {

                            }
                    }
                }
            _membersList.postValue(
                deferredMemberList.awaitAll().map { result ->
                    if (result.isSuccess) {
                        result.getOrNull() ?: return@launch
                    } else {
                        val throwable = result.exceptionOrNull() ?: return@launch
                        setNetworkDialogEvent()
                        return@launch
                    }
                }
            )
        }
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)
        _networkDialogEvent.postValue(Event(true))
    }
}
