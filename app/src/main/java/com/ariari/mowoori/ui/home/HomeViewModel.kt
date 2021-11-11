package com.ariari.mowoori.ui.home

import android.animation.Animator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.ariari.mowoori.ui.register.entity.UserInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.TimberUtil
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

    private val _groupInfoList = MutableLiveData<List<GroupInfo>>()
    val groupInfoList: LiveData<List<GroupInfo>> = _groupInfoList

    private var _isSnowing = MutableLiveData<Boolean>()
    val isSnowing: LiveData<Boolean> = _isSnowing

    private var _snowmanLevel = MutableLiveData<SnowmanLevel>()
    val snowmanLevel: LiveData<SnowmanLevel> = _snowmanLevel

    private var _isFirstCycle = true
    val isFirstCycle = _isFirstCycle

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
        val tempGroupList = mutableListOf<GroupInfo>()
        userInfo.groupList.forEachIndexed { index, groupId ->
            databaseReference.child("groups").child(groupId).get().addOnSuccessListener {
                val groupInfoJson = it.value ?: return@addOnSuccessListener
                val groupInfo = gson.fromJson(groupInfoJson.toString(), GroupInfo::class.java)
                if (index == 0) {
                    groupInfo.selected = true
                    _currentGroupInfo.value = groupInfo
                }
                tempGroupList.add(groupInfo)
                _groupInfoList.value = tempGroupList
            }.addOnFailureListener {
                // TODO: 실패처리
            }
        }
    }

    fun setCurrentGroupInfo(position: Int) {
        val tempGroupList = _groupInfoList.value?.mapIndexed { index, groupInfo ->
            when (index) {
                // 헤더를 포함한 위치 값이기 떄문에 -1 을 해주어야 한다.
                position - 1 -> {
                    groupInfo.selected = true
                    _currentGroupInfo.value = groupInfo
                }
                else -> groupInfo.selected = false
            }
            groupInfo
        }
        tempGroupList ?: return
        _groupInfoList.value = tempGroupList.requireNoNulls()
    }

    fun setIsFirstCycle(isFirst:Boolean){
        _isFirstCycle = isFirst
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
        if(!snowAnimList.contains(anim)){
            snowAnimList.add(anim)
        }
    }

    fun cancelSnowAnimList() {
        snowAnimList.forEach {
            it.cancel()
        }
    }
}
