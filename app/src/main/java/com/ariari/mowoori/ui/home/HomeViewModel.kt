package com.ariari.mowoori.ui.home

import android.animation.Animator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.HomeRepository
import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.TimberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _userInfo = MutableLiveData<Event<UserInfo>>()
    val userInfo: LiveData<Event<UserInfo>> = _userInfo

    private val _currentGroupInfo = MutableLiveData<Group>()
    val currentGroupInfo: LiveData<Group> = _currentGroupInfo
    val currentGroupName: LiveData<String> =
        Transformations.map(currentGroupInfo) { group -> group.groupInfo.groupName }

    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> = _groupList

    private var _isSnowing = MutableLiveData(true)
    val isSnowing: LiveData<Boolean> = _isSnowing

    private var _snowmanLevel = MutableLiveData<SnowmanLevel>()
    val snowmanLevel: LiveData<SnowmanLevel> = _snowmanLevel

    private val snowAnimList: MutableList<Animator> = mutableListOf()

    fun setUserInfo() {
        val uid = homeRepository.getUserUid()
        uid?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val result = homeRepository.getUserInfo(it)
                result.onSuccess { userInfo ->
                    _userInfo.postValue(Event(userInfo))
                }.onFailure {
                    TimberUtil.timber("setUserInfo*()", "$it")// TODO: 실패처리
                }
            }
        }
    }

    fun setGroupInfoList(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList =
                userInfo.groupList.map { groupId -> async { homeRepository.getGroup(groupId) } }
            val groupList = deferredList.awaitAll().mapNotNull { result ->
                val group = result.getOrNull()
                group?.apply {
                    if (this.groupId == userInfo.currentGroupId) {
                        this.selected = true
                        _currentGroupInfo.postValue(this)
                    }
                }
            }
            _groupList.postValue(groupList)
        }
    }

    fun setCurrentGroupInfo(groupId: String) {
        _groupList.value?.let { groupList ->
            groupList.forEach {
                it.selected = it.groupId == groupId
                if (it.selected) _currentGroupInfo.postValue(it)
            }
        }
        homeRepository.setCurrentGroupId(groupId)
    }

    fun updateIsSnowing() {
        if (isSnowing.value == null) {
            _isSnowing.postValue(true)
        } else {
            _isSnowing.postValue(!isSnowing.value!!)
        }
    }

    fun updateSnowmanLevel(snowmanLevel: SnowmanLevel) {
        _snowmanLevel.postValue(snowmanLevel)
    }

    fun addSnowAnim(anim: Animator) {
        if (!snowAnimList.contains(anim)) {
            snowAnimList.add(anim)
        }
    }

    fun addSnowAnim(animators: List<Animator>) {
        animators.forEach { anim ->
            addSnowAnim(anim)
        }
    }

    fun cancelSnowAnimList() {
        snowAnimList.forEach {
            it.removeAllListeners()
            it.cancel()
        }
    }
}
