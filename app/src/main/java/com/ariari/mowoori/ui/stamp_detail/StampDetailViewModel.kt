package com.ariari.mowoori.ui.stamp_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StampDetailViewModel @Inject constructor(
    private val stampsRepository: StampsRepository
) : ViewModel() {
    private val _closeBtnClick = MutableLiveData<Event<Boolean>>()
    val closeBtnClick: LiveData<Event<Boolean>> get() = _closeBtnClick

    private val _isCertify = MutableLiveData<Event<Boolean>>()
    val isCertify: LiveData<Event<Boolean>> get() = _isCertify

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _missionId = MutableLiveData<String>()
    val missionId: LiveData<String> get() = _missionId

    private val _missionName = MutableLiveData<String>()
    val missionName: LiveData<String> get() = _missionName

    private val _comment = MutableLiveData<String>()
    val comment: LiveData<String> get() = _comment

    private val _pictureUrl = MutableLiveData<String>()
    val pictureUrl: LiveData<String> get() = _pictureUrl

    private val _isStampPosted = MutableLiveData<Event<Unit>>()
    val isStampPosted: LiveData<Event<Unit>> get() = _isStampPosted

    fun setCloseBtnClick() {
        _closeBtnClick.value = Event(true)
    }

    fun setUserName(userName: String) {
        _userName.value = userName
    }

    fun setMissionId(missionId: String) {
        _missionId.value = missionId
        LogUtil.log("viewModelsetMissionId", _missionName.value.toString())
    }

    fun setMissionName(missionName: String) {
        _missionName.value = missionName
    }

    fun setComment(comment: String) {
        _comment.value = comment
    }

    fun setPictureUrl(url: String) {
        _pictureUrl.value = url
    }

    fun setIsCertify(detailMode: DetailMode) {
        println("StampDetail - $detailMode")
        when (detailMode) {
            DetailMode.INQUIRY -> _isCertify.value = Event(false)
            DetailMode.CERTIFY -> _isCertify.value = Event(true)
        }
    }

    fun postStamp() {
        // TODO: 1. stamps에 추가 - 이때 push.key로 id들고옴
        // TODO: 2. missions에 stampList에 id추가
        LogUtil.log("logmissionId", missionId.value.toString())
        viewModelScope.launch(IO) {
            val stampInfo = StampInfo(pictureUrl.value!!, comment.value!!, getCurrentDate())
            Timber.d(stampInfo.toString())

            stampsRepository.getMissionInfo(missionId.value!!.toString()).onSuccess {
                LogUtil.log("getMissionInfo", it.toString())

                stampsRepository.postStamp(stampInfo, Mission(missionId.value!!, it)).onSuccess {
                    _isStampPosted.postValue(Event(Unit))
                }.onFailure {
                    // onFailure
                }
            }.onFailure {
                throw Exception("stampInfo is not Posted.")
            }
        }
    }
}
