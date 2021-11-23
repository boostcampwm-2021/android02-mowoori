package com.ariari.mowoori.ui.stamp_detail

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StampDetailViewModel @Inject constructor(
    private val stampsRepository: StampsRepository,
) : ViewModel() {
    lateinit var detailInfo: DetailInfo
        private set

    lateinit var stampInfo: StampInfo
        private set

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> get() = _loadingEvent

    private val _closeBtnClick = MutableLiveData<Boolean>()
    val closeBtnClick: LiveData<Boolean> = _closeBtnClick

    private val _isCertify = MutableLiveData<Event<Boolean>>()
    val isCertify: LiveData<Event<Boolean>> get() = _isCertify

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _missionName = MutableLiveData<String>()
    val missionName: LiveData<String> get() = _missionName

    private val _comment = MutableLiveData("미션 완료.")
    val comment: LiveData<String> get() = _comment

    private val _pictureUri = MutableLiveData("default".toUri())
    val pictureUri: LiveData<Uri> get() = _pictureUri

    private val _isStampPosted = MutableLiveData<Event<Unit>>()
    val isStampPosted: LiveData<Event<Unit>> get() = _isStampPosted

    private val _networkDialogEvent = MutableLiveData<Boolean>()
    val networkDialogEvent: LiveData<Boolean> get() = _networkDialogEvent

    private val _groupMembersTokenList = MutableLiveData<List<String>>()
    val groupMembersTokenList: LiveData<List<String>> = _groupMembersTokenList

    private val _isFcmSent = MutableLiveData<Event<Unit>>()
    val isFcmSent: LiveData<Event<Unit>> = _isFcmSent

    private var _requestCount = 0
    private val requestCount get() = _requestCount

    private fun initRequestCount() {
        _requestCount = 0
    }

    private fun addRequestCount() {
        _requestCount++
    }

    private fun checkRequestCount() {
        if (requestCount == 1) {
            setNetworkDialogEvent()
        }
    }

    fun setDetailInfo(_detailInfo: DetailInfo) {
        detailInfo = _detailInfo
    }

    fun setLoadingEvent(flag: Boolean) {
        _loadingEvent.postValue(Event(flag))
    }

    fun setCloseBtnClick() {
        _closeBtnClick.value = true
    }

    fun setUserName() {
        _userName.value = detailInfo.userName
    }

    fun setMissionName() {
        _missionName.value = detailInfo.missionName
    }

    fun setComment(comment: String) {
        _comment.value = comment
    }

    fun setPictureUri(uri: Uri?) {
        uri?.let {
            _pictureUri.value = it
        }
    }

    fun setIsCertify() {
        when (detailInfo.detailMode) {
            DetailMode.INQUIRY -> _isCertify.value = Event(false)
            DetailMode.CERTIFY -> _isCertify.value = Event(true)
        }
    }

    fun postStamp() {
        viewModelScope.launch(IO) {
            initRequestCount()
            stampsRepository.putCertificationImage(pictureUri.value!!, detailInfo.missionId)
                .onSuccess { uri ->
                    LogUtil.log("stamp", uri)
                    initRequestCount()
                    stampsRepository.getMissionInfo(detailInfo.missionId)
                        .onSuccess {
                            stampInfo = StampInfo(
                                uri, comment.value!!, getCurrentDate()
                            )
                            stampsRepository.postStamp(stampInfo, Mission(detailInfo.missionId, it))
                                .onSuccess {
                                    _isStampPosted.postValue(Event(Unit))
                                }.onFailure { 
                                    addRequestCount()
                                    checkRequestCount()
                                }
                        }
                        .onFailure {
                            addRequestCount()
                            checkRequestCount()
                        }
                }
                .onFailure {
                    addRequestCount()
                    checkRequestCount()
                }
        }
    }

    fun getGroupMembersFcmToken() {
        viewModelScope.launch(Dispatchers.IO) {
            stampsRepository.getGroupMembersUserId().onSuccess { idList ->
                val deferredMembersUserIdList = idList.map { userId ->
                    async { stampsRepository.getGroupMembersFcmToken(userId) }
                }
                _groupMembersTokenList.postValue(
                    deferredMembersUserIdList.awaitAll().map { result -> result.getOrNull() ?: "" })
            }.onFailure {
                LogUtil.log(it.message.toString())
            }
        }
    }

    fun postFcm() {
        viewModelScope.launch {
            groupMembersTokenList.value?.let { tokenList ->
                tokenList.forEach { fcmToken ->
                    stampsRepository.postFcmMessage(
                        fcmToken,
                        detailInfo.copy(
                            detailMode = DetailMode.INQUIRY,
                            stampInfo = stampInfo
                        )
                    ).onSuccess {
                        LogUtil.log("fcm", it.success.toString())
                        LogUtil.log("fcm", it.failure.toString())
                    }.onFailure {
                        LogUtil.log("fcm", it.message.toString())
                    }
                }
                setLoadingEvent(false)
                _isFcmSent.postValue(Event(Unit))
            }
        }
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)
        _networkDialogEvent.postValue(true)
    }
}
