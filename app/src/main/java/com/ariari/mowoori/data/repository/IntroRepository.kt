package com.ariari.mowoori.data.repository

import androidx.lifecycle.LiveData
import com.ariari.mowoori.util.Event
import com.google.firebase.database.DataSnapshot

interface IntroRepository {
    fun checkUserRegistered(userUid: String): LiveData<Event<Boolean>>
}
