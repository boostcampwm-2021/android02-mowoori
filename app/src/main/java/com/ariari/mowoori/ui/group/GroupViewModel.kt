package com.ariari.mowoori.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.ui.home.entity.GroupInfo
import com.google.firebase.database.FirebaseDatabase

class GroupViewModel : ViewModel() {
    // TODO: Hilt 인스턴스 주입
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference

    private val _groupName = MutableLiveData<String>()
    private val _isValid = Transformations.map(_groupName) { groupName ->
        groupName.length in 1..8
    }
    val isValid: LiveData<Boolean> = _isValid

    fun checkGroupNameValidation(name: String) {
        _groupName.value = name
    }

    // TODO: 레포지토리에 실행되어야할 코드
    fun addNewGroup() {
        // TODO: 유저 아이디 가져오기

        val name = _groupName.value ?: return
        val groupInfo = GroupInfo(name)
        val groupId = databaseReference.child("groups").push().key ?: return
        databaseReference.child("groups").child(groupId).setValue(groupInfo)

        // TODO: 가져온 유저의 그룹 리스트에 생성한 그룹 추가
    }
}
