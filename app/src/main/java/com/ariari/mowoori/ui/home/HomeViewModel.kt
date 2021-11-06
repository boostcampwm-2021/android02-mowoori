package com.ariari.mowoori.ui.home

import android.animation.Animator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class HomeViewModel : ViewModel() {
    // TODO: Hilt 인스턴스 주입
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference
    private val gson = Gson()

    private val _userInfo = MutableLiveData<Event<UserInfo>>()
    val userInfo: LiveData<Event<UserInfo>> = _userInfo

    private val _currentGroupInfo = MutableLiveData<GroupInfo>()
    val currentGroupInfo: LiveData<GroupInfo> = _currentGroupInfo

    private val mutableGroupList = mutableListOf<GroupInfo>()
    private val _groupInfoList = MutableLiveData<List<GroupInfo>>()
    val groupInfoList: LiveData<List<GroupInfo>> = _groupInfoList

    private var _isSnowing = MutableLiveData<Boolean>()
    val isSnowing: LiveData<Boolean> = _isSnowing

    private var _snowmanLevel = MutableLiveData<SnowmanLevel>()
    val snowmanLevel: LiveData<SnowmanLevel> = _snowmanLevel

    private val snowAnimList: MutableList<Animator> = mutableListOf()

    fun setUserInfo() {
        // TODO: 레포지토리에서 실행되는 코드
        val tempUserId = "kldaji"
        databaseReference.child("users").child(tempUserId).get().addOnSuccessListener {
            val userInfoJson = it.value ?: return@addOnSuccessListener
            val userInfo = gson.fromJson(userInfoJson.toString(), UserInfo::class.java)
            _userInfo.value = Event(userInfo)
        }.addOnFailureListener {
            // TODO: 실패처리
        }
    }

    fun setGroupInfoList(userInfo: UserInfo) {
        userInfo.groupList.forEachIndexed { index, groupId ->
            databaseReference.child("groups").child(groupId).get().addOnSuccessListener {
                val groupInfoJson = it.value ?: return@addOnSuccessListener
                val groupInfo = gson.fromJson(groupInfoJson.toString(), GroupInfo::class.java)
                if (index == 0) {
                    groupInfo.selected = true
                    _currentGroupInfo.value = groupInfo
                }
                mutableGroupList.add(groupInfo)
                _groupInfoList.value = mutableGroupList
            }.addOnFailureListener {
                // TODO: 실패처리
            }
        }
    }

    fun setCurrentGroupInfo(position: Int) {
        mutableGroupList.forEach { groupInfo ->
            groupInfo.selected = false
        }
        // 헤더를 포함한 위치 값이기 떄문에 -1 을 해주어야 한다.
        mutableGroupList[position - 1].selected = true
        _groupInfoList.value = mutableGroupList
        _currentGroupInfo.value = mutableGroupList[position - 1]
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
        snowAnimList.add(anim)
    }

    fun cancelSnowAnimList() {
        snowAnimList.forEach {
            it.cancel()
        }
    }
}
