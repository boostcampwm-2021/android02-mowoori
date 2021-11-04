package com.ariari.mowoori.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ariari.mowoori.util.Event
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SplashViewModel : ViewModel() {
    private val database: DatabaseReference = Firebase.database.reference
    private val _isUserRegistered = MutableLiveData<Boolean>()
    val isUserRegistered: LiveData<Boolean> = _isUserRegistered

    fun checkUserRegistered(userUid: String) {
        database.child("users").child(userUid).get().addOnCompleteListener {
            val snapshot = it.result
            _isUserRegistered.value = snapshot.value != null
        }
    }
}
