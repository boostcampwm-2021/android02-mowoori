package com.ariari.mowoori.ui.home

import android.animation.Animator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.HomeRepository
import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {
    private val _userInfo = MutableLiveData<Event<UserInfo>>()
    val userInfo: LiveData<Event<UserInfo>> = _userInfo

    private val _currentGroupInfo = MutableLiveData<Group>()
    val currentGroupInfo: LiveData<Group> = _currentGroupInfo
    val snowmanLevel: LiveData<SnowmanLevel> =
        Transformations.map(currentGroupInfo) { group -> setSnowmanLevel(group.groupInfo.doneMission) }

    val currentGroupName: LiveData<String> =
        Transformations.map(currentGroupInfo) { group -> group.groupInfo.groupName }

    private val _groupList = MutableLiveData<List<Group>>()
    val groupList: LiveData<List<Group>> = _groupList

    private var _isSnowing = MutableLiveData(true)
    val isSnowing: LiveData<Boolean> = _isSnowing

    private var _alphaForLv4 = MutableLiveData(0f)
    val alphaForLv4: LiveData<Float> = _alphaForLv4

    private val snowAnimatorList: MutableList<Animator> = mutableListOf()

    private val animatorList: MutableList<Animator> = mutableListOf()

    fun resetAlphaForLv4() {
        _alphaForLv4.value = 0f
    }

    fun setUserInfo() {
        val uid = homeRepository.getUserUid()
        uid?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val result = homeRepository.getUserInfo(it)
                result.onSuccess { userInfo ->
                    _userInfo.postValue(Event(userInfo))
                }.onFailure {
                    LogUtil.log("setUserInfo*()", "$it")// TODO: 실패처리
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

    private fun setSnowmanLevel(doneMission: Int): SnowmanLevel {
        return when {
            doneMission <= 1 -> SnowmanLevel.LV1
            doneMission == 2 -> SnowmanLevel.LV2
            doneMission == 3 -> SnowmanLevel.LV3
            else -> SnowmanLevel.LV4
        }
    }

    fun addSnowAnimator(anim: Animator) {
        if (!snowAnimatorList.contains(anim)) {
            snowAnimatorList.add(anim)
        }
    }


    fun addAnimator(anim: Animator) {
        if (!animatorList.contains(anim)) {
            animatorList.add(anim)
        }
    }

    fun cancelSnowAnimator() {
        snowAnimatorList.forEach {
            it.removeAllListeners()
            it.cancel()
        }
    }

    fun cancelAnimator() {
        animatorList.forEach {
            it.removeAllListeners()
            it.cancel()
        }
    }

    private val _isBodyMeasured = MutableLiveData<Boolean>()
    val isBodyMeasured: LiveData<Boolean> get() = _isBodyMeasured
    private val _isLeftBlackViewInfoDone = MutableLiveData<Boolean>()
    private val _isLeftWhiteViewInfoDone = MutableLiveData<Boolean>()
    private val _isRightBlackViewInfoDone = MutableLiveData<Boolean>()
    private val _isRightWhiteViewInfoDone = MutableLiveData<Boolean>()
    private val _viewInfoMediator = MediatorLiveData<Boolean>()
    val viewInfoMediator: LiveData<Boolean> = _viewInfoMediator

    fun addSources() {
        with(_viewInfoMediator) {
            addSource(_isLeftBlackViewInfoDone) {
                this.value = isViewInfoDone()
            }
            addSource(_isLeftWhiteViewInfoDone) {
                this.value = isViewInfoDone()
            }
            addSource(_isRightBlackViewInfoDone) {
                this.value = isViewInfoDone()
            }
            addSource(_isRightWhiteViewInfoDone) {
                this.value = isViewInfoDone()
            }
        }
    }

    fun bodyMeasured() {
        _isBodyMeasured.value = true
    }

    private fun isViewInfoDone(): Boolean {
        return _isLeftBlackViewInfoDone.value == true && _isLeftWhiteViewInfoDone.value == true && _isRightBlackViewInfoDone.value == true && _isRightWhiteViewInfoDone.value == true
    }

    fun leftBlackViewInfoDone() {
        _isLeftBlackViewInfoDone.value = true
    }

    fun leftWhiteViewInfoDone() {
        _isLeftWhiteViewInfoDone.value = true
    }

    fun rightBlackViewInfoDone() {
        _isRightBlackViewInfoDone.value = true
    }

    fun rightWhiteViewInfoDone() {
        _isRightWhiteViewInfoDone.value = true
    }

}
