package com.ariari.mowoori.ui.stamp_detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.ErrorMessage
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var stampInfo = StampInfo()

    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent

    private val _closeBtnClick = MutableLiveData<Boolean>()
    val closeBtnClick: LiveData<Boolean> = _closeBtnClick

    private val _isCertify = MutableLiveData<Event<Boolean>>()
    val isCertify: LiveData<Event<Boolean>> = _isCertify

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _missionName = MutableLiveData<String>()
    val missionName: LiveData<String> = _missionName

    private val _comment = MutableLiveData<String>()
    val comment: LiveData<String> = _comment

    private val _pictureUri = MutableLiveData<Uri>()
    val pictureUri: LiveData<Uri> = _pictureUri

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
            if (pictureUri.value != null) {
                initRequestCount()
                stampsRepository.putCertificationImage(pictureUri.value!!, detailInfo.missionId)
                    .onSuccess { uri ->
                        postStampInfo(uri)
                    }
                    .onFailure {
                        checkThrowableMessage(it)
                    }
            } else {
                postStampInfo("")
            }
        }
    }

    fun getGroupMembersFcmToken() {
        viewModelScope.launch(IO) {
            stampsRepository.getGroupMembersUserId()
                .onSuccess { idList ->
                    val deferredMembersUserIdList = idList.map { userId ->
                        async { stampsRepository.getGroupMembersFcmToken(userId) }
                    }
                    _groupMembersTokenList.postValue(
                        deferredMembersUserIdList.awaitAll().map { result ->
                            if (result.isSuccess) {
                                result.getOrNull() ?: ""
                            } else {
                                val throwable = result.exceptionOrNull() ?: return@launch
                                checkThrowableMessage(throwable)
                                return@launch
                            }
                        }
                    )
                }.onFailure {
                    checkThrowableMessage(it)
                }
        }
    }

    fun postFcm() {
        viewModelScope.launch {
            groupMembersTokenList.value?.let { tokenList ->
                tokenList.forEach { fcmToken ->
                    initRequestCount()
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
                        checkThrowableMessage(it)
                        LogUtil.log("fcm", it.message.toString())
                    }
                }
                setLoadingEvent(false)
                _isFcmSent.postValue(Event(Unit))
            }
        }
    }

    private suspend fun postStampInfo(uriString: String) {
        LogUtil.log("stamp", uriString)
        initRequestCount()
        stampsRepository.getMissionInfo(detailInfo.missionId)
            .onSuccess {
                val stampInfo = StampInfo(uriString, comment.value!!, getCurrentDate())
                initRequestCount()
                stampsRepository.postStamp(stampInfo, Mission(detailInfo.missionId, it))
                    .onSuccess {
                        _isStampPosted.postValue(Event(Unit))
                    }.onFailure { throwable ->
                        checkThrowableMessage(throwable)
                    }
            }
            .onFailure {
                checkThrowableMessage(it)
            }
    }

    private fun checkThrowableMessage(throwable: Throwable) {
        when (throwable.message) {
            ErrorMessage.Offline.message -> {
                addRequestCount()
                checkRequestCount()
            }
            else -> setLoadingEvent(false)
        }
    }

    private fun setNetworkDialogEvent() {
        setLoadingEvent(false)
        _networkDialogEvent.postValue(true)
    }
}
