package com.ariari.mowoori.ui.stamp_detail

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariari.mowoori.data.repository.StampsRepository
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.Event
import com.ariari.mowoori.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    private val _isNetworkDialogShowed = MutableLiveData(false)
    val isNetworkDialogShowed: LiveData<Boolean> get() = _isNetworkDialogShowed

    private val _isFcmSent = MutableLiveData<Boolean>()
    val isFcmSent: LiveData<Boolean> = _isFcmSent

    private val _checkCommentValidEvent = MutableLiveData<Event<Unit>>()
    val checkCommentValidEvent: LiveData<Event<Unit>> = _checkCommentValidEvent

    fun resetNetworkDialog() {
        _isNetworkDialogShowed.value = false
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

    fun checkCommentValid() {
        _checkCommentValidEvent.postValue(Event(Unit))
    }

    fun postStamp() = viewModelScope.launch(Dispatchers.IO) {
        try {
            var imageUrl = ""
            pictureUri.value?.let { uri ->
                imageUrl = stampsRepository.putCertificationImage(uri,
                    detailInfo.missionId).getOrThrow()
            }
            val missionInfo = stampsRepository.getMissionInfo(detailInfo.missionId).getOrThrow()
            stampInfo = StampInfo(imageUrl, comment.value!!, getCurrentDate())
            stampsRepository.postStamp(stampInfo, Mission(detailInfo.missionId, missionInfo))
            val doneMissionJob = putGroupDoneMission(missionInfo)
            val stampPostedJob = getGroupMembersFcmToken()
            joinAll(doneMissionJob, stampPostedJob)
        } catch (e: Exception) {
            checkNetworkDialog()
        } catch (e: NullPointerException) {
            // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
        }
    }

    private fun putGroupDoneMission(missionInfo: MissionInfo) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (missionInfo.totalStamp - missionInfo.curStamp == 1) {
                    stampsRepository.putGroupDoneMission().getOrThrow()
                }
            } catch (e: Exception) {
                // TODO: postStamp 이후 다이얼로그 처리
//                checkNetworkDialog()
            } catch (e: NullPointerException) {
                // 파이어베이스 구조가 잘 짜여있다면 여기에 도달할 수 없다.
            }
        }

    private fun getGroupMembersFcmToken() = viewModelScope.launch(Dispatchers.IO) {
        val userIdList = stampsRepository.getGroupMembersUserId().getOrThrow()
        val deferredFcmTokenList = userIdList.map { userId ->
            async { stampsRepository.getGroupMembersFcmToken(userId) }
        }
        val fcmTokenList = deferredFcmTokenList.awaitAll().map { result ->
            try {
                result.getOrThrow()
            } catch (e: Exception) {
                // TODO: postStamp 이후 다이얼로그 처리
//                checkNetworkDialog()
                return@launch
            } catch (e: NullPointerException) {
                return@launch
            }
        }
        val postMessageJobList = fcmTokenList.map { fcmToken ->
            postMessage(fcmToken)
        }.apply { this.joinAll() }
        postMessageJobList.forEach {
            if (it.isCancelled) return@launch
        }
        setLoadingEvent(false)
        _isFcmSent.postValue(true)
    }

    // retrofit
    private fun postMessage(fcmToken: String) = viewModelScope.launch {
        try {
            stampsRepository.postFcmMessage(fcmToken,
                detailInfo.copy(detailMode = DetailMode.INQUIRY, stamp = Stamp("", stampInfo)))
                .getOrThrow()
        } catch (e: HttpException) {
            // retrofit exception
            // TODO: retrofit url error 처리
        } catch (e: Exception) {
//             TODO: postStamp 이후 다이얼로그 처리
//            checkNetworkDialog()
            this.cancel()
        } catch (e: NullPointerException) {
            this.cancel()
        }
    }

    private fun checkNetworkDialog() {
        setLoadingEvent(false)
        _isNetworkDialogShowed.value?.let {
            if (!it) _isNetworkDialogShowed.postValue(true)
        }
    }
}
