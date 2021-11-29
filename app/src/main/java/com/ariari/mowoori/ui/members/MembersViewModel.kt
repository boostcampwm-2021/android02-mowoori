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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
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

    private val _isNetworkDialogShowed = MutableLiveData(Event(false))
    val isNetworkDialogShowed: LiveData<Event<Boolean>> get() = _isNetworkDialogShowed

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = Event(false)
    }

    fun setLoadingEvent(isLoading: Boolean) {
        _loadingEvent.postValue(Event(isLoading))
    }

    fun fetchGroupInfo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val group = membersRepository.getCurrentGroupInfo().getOrThrow()
            _currentGroup.postValue(group)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    fun clickPlusButton() {
        _openInviteDialogEvent.value = Event(currentGroup.value?.groupId)
    }

    fun fetchMemberList() = viewModelScope.launch(Dispatchers.IO) {
        val deferredMemberList =
            requireNotNull(currentGroup.value).groupInfo.userList.map { userId ->
                async { membersRepository.getUserInfo(userId) }
            }
        _membersList.postValue(
            deferredMemberList.awaitAll().map { result ->
                try {
                    result.getOrThrow()
                } catch (e: Exception) {
                    checkNetworkDialog()
                    return@launch
                } catch (e: NullPointerException) {
                    return@launch
                }
            }
        )
    }

    private fun checkNetworkDialog() {
        setLoadingEvent(false)
        _isNetworkDialogShowed.value?.let {
            if (!it.peekContent()) {
                _isNetworkDialogShowed.postValue(Event(true))
            }
        }
    }
}
