package com.ariari.mowoori.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ariari.mowoori.util.Event
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class IntroRepositoryImpl @Inject constructor(
    private val firebaseReference: DatabaseReference
) : IntroRepository {
    override fun checkUserRegistered(userUid: String): LiveData<Event<Boolean>> {
        val resultData = MutableLiveData<Event<Boolean>>()
        firebaseReference.child("users").child(userUid).get().addOnCompleteListener {
            resultData.value = Event(it.result.value != null)
        }
        return resultData
    }
}
