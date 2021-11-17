package com.ariari.mowoori.ui.stamp_detail

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampDetailBinding
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp_detail.entity.PictureType
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.PictureDialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StampDetailFragment :
    BaseFragment<FragmentStampDetailBinding>(R.layout.fragment_stamp_detail) {
    private val safeArgs: StampDetailFragmentArgs by navArgs()
    private val viewModel: StampDetailViewModel by viewModels()
    private lateinit var detailInfo: DetailInfo

    private val activityGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.setPictureUri(it)
            Glide.with(requireContext())
                .load(it)
                .override(300, 300)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(binding.ivStampDetail)
            binding.tvStampDetailIcon.isVisible = false
        }

    private val activityPictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                LogUtil.log("takePictureContent", success.toString())
            } else {
                LogUtil.log("takePictureContent", success.toString())
            }
        }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                activityPictureLauncher.launch("image/temp".toUri())
            } else {
                toastMessage("사진 촬영을 위해서는 카메라 권한이 필요합니다.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setDetailInfo()
        setEditMode()
        setBtnVisible()
        setDetailTransitionName()
        setUserName()
        setMissionId()
        setMissionName()
        setPicture()
        setComment()
        setRootClick()
        setIsMissionPostedObserver()
        setCloseBtnClickObserver()
        setIsCertifyObserver()
        setBtnCertifyListener()
        setCommentObserver()
    }

    private fun setDetailInfo() {
        detailInfo = safeArgs.detailInfo
    }

    private fun setBtnVisible() {
        viewModel.setIsCertify(detailInfo.detailMode)
    }

    private fun setDetailTransitionName() {
        binding.ivStampDetail.transitionName = detailInfo.stampInfo.pictureUrl
    }

    private fun setUserName() {
        viewModel.setUserName(detailInfo.userName)
    }

    private fun setMissionId() {
        viewModel.setMissionId(detailInfo.missionId)
    }

    private fun setMissionName() {
        viewModel.setMissionName(detailInfo.missionName)
    }

    private fun setEditMode() {
        if (detailInfo.detailMode == DetailMode.INQUIRY) {
            binding.etStampDetailComment.keyListener = null
        }
    }

    private fun setComment() {
        viewModel.setComment(detailInfo.stampInfo.comment)
    }

    private fun setPicture() {
        binding.ivStampDetail.setOnClickListener {
            PictureDialogFragment(onClick).show(
                requireActivity().supportFragmentManager,
                "PictureDialogFragment"
            )
        }
        if (detailInfo.stampInfo.pictureUrl != "") {
            Glide.with(requireContext())
                .load(detailInfo.stampInfo.pictureUrl)
                .override(300, 300)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(binding.ivStampDetail)
            binding.tvStampDetailIcon.isInvisible = true
        }
    }

    private val onClick: (pictureType: PictureType) -> Unit = {
        when (it) {
            PictureType.CAMERA -> {
                takePicture(android.Manifest.permission.CAMERA)
            }
            else -> {
                activityGalleryLauncher.launch("image/*")
            }
        }
    }

    private fun takePicture(permission: String) {
        if (!hasPermission(permission)) {
            activityPermissionLauncher.launch(permission)
        } else {
            Timber.d("모든 권한이 승인되어 있어서 사진찍기 가능")
            activityPictureLauncher.launch("image/temp".toUri())
        }
    }

    private fun hasPermission(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toastMessage("사진 촬영을 위해서는 카메라 권한이 필요합니다.")
            return false
        }
        return true
    }

    private fun setRootClick() {
        binding.container.setOnClickListener {
            hideKeyboard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }

    private fun hideKeyboard(v: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun setCloseBtnClickObserver() {
        viewModel.closeBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setIsCertifyObserver() {
        viewModel.isCertify.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.btnStampDetailCertify.isVisible = true
                binding.tvStampDetailComment.isFocusable = true
            } else {
                binding.btnStampDetailCertify.isInvisible = true
                binding.tvStampDetailComment.isFocusable = false
            }
        })
    }

    private fun setCommentObserver() {
        viewModel.comment.observe(viewLifecycleOwner) {
            binding.etStampDetailComment.setText(it)
        }
    }

    private fun setBtnCertifyListener() {
        binding.btnStampDetailCertify.setOnClickListener {
            viewModel.setComment(binding.etStampDetailComment.text.toString())
            viewModel.postStamp()
        }
    }

    private fun setIsMissionPostedObserver() {
        viewModel.isStampPosted.observe(viewLifecycleOwner, EventObserver {
            // TODO: 알림 발생
            this.findNavController().popBackStack()
        })
    }
}
