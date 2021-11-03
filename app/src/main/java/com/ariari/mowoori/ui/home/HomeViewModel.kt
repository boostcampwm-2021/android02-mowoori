package com.ariari.mowoori.ui.home

import android.animation.AnimatorSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private var _isSnowing = MutableLiveData<Boolean>()
    val isSnowing: LiveData<Boolean> = _isSnowing

    private var _snowmanLevel = MutableLiveData<SnowmanLevel>()
    val snowmanLevel: LiveData<SnowmanLevel> = _snowmanLevel

    private val snowAnimSets: MutableList<AnimatorSet> = mutableListOf()

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

    fun addSnowAnimSet(set: AnimatorSet) {
        snowAnimSets.add(set)
    }

    fun cancelSnowAnimSets() {
        snowAnimSets.forEach {
            it.cancel()
        }
    }
}
