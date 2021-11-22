package com.ariari.mowoori.ui.stamp_detail

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampDetailBinding
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp_detail.entity.PictureType
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.LogUtil
import com.ariari.mowoori.util.getCurrentDateTime
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.PictureDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class StampDetailFragment :
    BaseFragment<FragmentStampDetailBinding>(R.layout.fragment_stamp_detail) {
    private val stampViewModel: StampDetailViewModel by viewModels()
    private val safeArgs: StampDetailFragmentArgs by navArgs()
    private var currentPhotoPath: String? = null
    private var providerUri: Uri? = null

    private val activityGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            saveCurrentPicture(it)
        }

    private val activityPictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                LogUtil.log("takePictureContent", success.toString())
                saveCurrentPicture(providerUri)
            } else {
                LogUtil.log("takePictureContent", success.toString())
            }
        }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                savePhoto()
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
        binding.viewModel = stampViewModel
        stampViewModel.setDetailInfo(safeArgs.detailInfo)
        init()
        setListener()
        setObserver()
        setDetailTransitionName()
    }

    private fun init() {
        setEditMode()
        setBtnVisible()
        setUserName()
        setMissionName()
        setComment()
    }

    private fun setListener() {
        setBtnCertifyListener()
        setPictureClickListener()
        setRootClick()
    }

    private fun setObserver() {
        setLoadingObserver()
        setIsMissionPostedObserver()
        setCloseBtnClickObserver()
        setIsCertifyObserver()
        setCommentObserver()
        setNetworkDialogObserver()
    }

    private fun setEditMode() {
        if (stampViewModel.detailInfo.detailMode == DetailMode.INQUIRY) {
            binding.etStampDetailComment.keyListener = null
        }
    }

    private fun setBtnVisible() {
        stampViewModel.setIsCertify()
    }

    private fun setDetailTransitionName() {
        binding.ivStampDetail.transitionName = stampViewModel.detailInfo.stampInfo.pictureUrl
    }

    private fun setUserName() {
        stampViewModel.setUserName()
    }

    private fun setMissionName() {
        stampViewModel.setMissionName()
    }

    private fun setComment() {
        stampViewModel.setComment(stampViewModel.detailInfo.stampInfo.comment)
    }

    private fun setPictureClickListener() {
        val pictureUrl = stampViewModel.detailInfo.stampInfo.pictureUrl
        if (pictureUrl != "") {
            loadPicture(pictureUrl)
        } else {
            binding.ivStampDetail.setOnClickListener {
                PictureDialogFragment(onClick).show(
                    requireActivity().supportFragmentManager,
                    "PictureDialogFragment"
                )
            }
        }
    }

    private fun loadPicture(pictureUrl: String) {
        Glide.with(requireContext())
            .load(pictureUrl)
            .override(300, 300)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(binding.ivStampDetail)
        binding.tvStampDetailIcon.isInvisible = true
    }

    private fun setBtnCertifyListener() {
        binding.btnStampDetailCertify.setOnClickListener {
            stampViewModel.setComment(binding.etStampDetailComment.text.toString())
            stampViewModel.postStamp()
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
            savePhoto()
        }
    }

    private fun savePhoto() {
        Timber.d("모든 권한이 승인되어 있어서 사진찍기 가능")
        val photoFile: File?
        try {
            photoFile = createImageFile()
            if (photoFile != null) {
                providerUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.ariari.mowoori.fileProvider",
                    photoFile
                )
                LogUtil.log("providerUri", providerUri.toString())
                activityPictureLauncher.launch(providerUri)
            }
        } catch (exception: Exception) {
            Timber.e("create Image File Error~~")
        }
    }

    private fun createImageFile(): File {
        Timber.d("createImageFile Start")
        val timeStamp = getCurrentDateTime()
        Timber.d(timeStamp)
        val imageFileName = "${stampViewModel.detailInfo.missionId}_$timeStamp.jpg"
        Timber.d(imageFileName)
        val storageDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString(),
            "Mowoori"
        )
        // LogUtil.log("storageDir", storageDir.toString())

        if (!storageDir.exists()) {
            // Timber.d("make new dir")
            storageDir.mkdirs()
        }
        val imageFile = File(storageDir, imageFileName)
        // LogUtil.log("imageFile", imageFile.toString())
        currentPhotoPath = imageFile.absolutePath
        // LogUtil.log("currentPhotoPath", currentPhotoPath.toString())
        return imageFile
    }

    private fun saveCurrentPicture(uri: Uri?) {
        stampViewModel.setPictureUri(uri)
        Glide.with(requireContext())
            .load(uri)
            .override(300, 300)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(binding.ivStampDetail)
        binding.tvStampDetailIcon.isVisible = false
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
        stampViewModel.closeBtnClick.observe(viewLifecycleOwner, {
            this.findNavController().popBackStack()
        })
    }

    private fun setIsCertifyObserver() {
        stampViewModel.isCertify.observe(viewLifecycleOwner, EventObserver {
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
        stampViewModel.comment.observe(viewLifecycleOwner) {
            binding.etStampDetailComment.setText(it)
        }
    }

    private fun setIsMissionPostedObserver() {
        stampViewModel.isStampPosted.observe(viewLifecycleOwner, EventObserver {
            // TODO: 알림 발생
            this.findNavController().popBackStack()
        })
    }

    private fun setLoadingObserver() {
        stampViewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
    }

    private fun setNetworkDialogObserver() {
        stampViewModel.networkDialogEvent.observe(viewLifecycleOwner, {
            NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
                override fun onCancelClick(dialog: DialogFragment) {
                    dialog.dismiss()
                    findNavController().navigate(R.id.action_stampsFragment_to_homeFragment)
                }

                override fun onRetryClick(dialog: DialogFragment) {
                    dialog.dismiss()
                    stampViewModel.postStamp()
                }
            }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
        })
    }
}
