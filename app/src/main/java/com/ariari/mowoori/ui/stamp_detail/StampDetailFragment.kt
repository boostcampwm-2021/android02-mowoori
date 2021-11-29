package com.ariari.mowoori.ui.stamp_detail

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
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
import com.ariari.mowoori.util.getVibrateAnimInstance
import com.ariari.mowoori.util.hideKeyBoard
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.PictureDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding4.view.clicks
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class StampDetailFragment :
    BaseFragment<FragmentStampDetailBinding>(R.layout.fragment_stamp_detail) {
    private val stampDetailViewModel: StampDetailViewModel by viewModels()
    private val safeArgs: StampDetailFragmentArgs by navArgs()
    private var currentPhotoPath: String? = null
    private var providerUri: Uri? = null
    private var completeBtnDisposable: Disposable? = null

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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = stampDetailViewModel
        stampDetailViewModel.setDetailInfo(safeArgs.detailInfo)
        init()
        setListener()
        setObserver()
        setDetailTransitionName()
    }

    private fun init() {
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
        setCloseBtnClickObserver()
        setIsCertifyObserver()
        setNetworkDialogObserver()
        setIsFcmSentObserver()
        setValidationObserver()
    }

    private fun setBtnVisible() {
        stampDetailViewModel.setIsCertify()
    }

    private fun setDetailTransitionName() {
        binding.ivStampDetail.transitionName = stampDetailViewModel.detailInfo.stamp.stampId
    }

    private fun setUserName() {
        stampDetailViewModel.setUserName()
    }

    private fun setMissionName() {
        stampDetailViewModel.setMissionName()
    }

    private fun setComment() {
        if (stampDetailViewModel.detailInfo.detailMode == DetailMode.INQUIRY) {
            stampDetailViewModel.setComment(stampDetailViewModel.detailInfo.stamp.stampInfo.comment)
            binding.etStampDetailComment.setText(stampDetailViewModel.detailInfo.stamp.stampInfo.comment)
            binding.etStampDetailComment.hint = "입력한 한줄평이 없어요."
            binding.etStampDetailComment.keyListener = null
        }
    }

    private fun setPictureClickListener() {
        val pictureUrl = stampDetailViewModel.detailInfo.stamp.stampInfo.pictureUrl
        val mode = stampDetailViewModel.detailInfo.detailMode

        if (mode == DetailMode.CERTIFY) {
            binding.ivStampDetail.setOnClickListener {
                PictureDialogFragment(onClick).show(
                    requireActivity().supportFragmentManager,
                    "PictureDialogFragment"
                )
            }
        } else {
            loadPicture(pictureUrl)
        }
    }

    private fun loadPicture(pictureUrl: String) {
        binding.tvStampDetailIcon.isInvisible = true
        if (pictureUrl == "") {
            // setDefaultImageUri()
            // 기본 이미지일 경우
            Glide.with(requireContext())
                .load(R.drawable.ic_stamp)
                .override(300, 300)
                .transform(CenterCrop(), RoundedCorners(16))
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(binding.ivStampDetail)
        } else {
            Glide.with(requireContext())
                .load(pictureUrl)
                .override(300, 300)
                .transform(CenterCrop(), RoundedCorners(16))
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(binding.ivStampDetail)
        }
    }

    private fun setBtnCertifyListener() {
        completeBtnDisposable = binding.btnStampDetailCertify.clicks()
            .throttleFirst(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                stampDetailViewModel.setComment(binding.etStampDetailComment.text.toString())
                if (requireContext().isNetWorkAvailable()) {
                    stampDetailViewModel.checkCommentValid()
                } else {
                    showNetworkDialog()
                }
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
            providerUri = FileProvider.getUriForFile(
                requireContext(),
                "com.ariari.mowoori.fileProvider",
                photoFile
            )
            LogUtil.log("providerUri", providerUri.toString())
            activityPictureLauncher.launch(providerUri)
        } catch (exception: Exception) {
            Timber.e("create Image File Error~~")
        }
    }

    private fun createImageFile(): File {
        Timber.d("createImageFile Start")
        val timeStamp = getCurrentDateTime()
        Timber.d(timeStamp)
        val imageFileName = "${stampDetailViewModel.detailInfo.missionId}_$timeStamp.jpg"
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
        stampDetailViewModel.setPictureUri(uri)

        if (uri == null) {
            binding.tvStampDetailIcon.isVisible = true
        } else {
            Glide.with(requireContext())
                .load(uri)
                .override(300, 300)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(binding.ivStampDetail)
            binding.tvStampDetailIcon.isVisible = false
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
            requireContext().hideKeyBoard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }

    private fun setCloseBtnClickObserver() {
        stampDetailViewModel.closeBtnClick.observe(viewLifecycleOwner, {
            this.findNavController().popBackStack()
        })
    }

    private fun setIsCertifyObserver() {
        stampDetailViewModel.isCertify.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                with(binding) {
                    btnStampDetailCertify.isVisible = true
                    tvStampDetailComment.isFocusable = true
                    ivStampDetailHighlightCircle.isVisible = true
                }
            } else {
                with(binding) {
                    btnStampDetailCertify.isInvisible = true
                    tvStampDetailComment.isFocusable = false
                    ivStampDetailHighlightCircle.isVisible = false
                }
            }
        })
    }

    private fun setIsFcmSentObserver() {
        stampDetailViewModel.isFcmSent.observe(viewLifecycleOwner, {

            if (it){
                val detailInfo = stampDetailViewModel.detailInfo
                this.findNavController()
                    .navigate(StampDetailFragmentDirections.actionStampDetailFragmentToStampsFragment(
                        detailInfo.missionId, detailInfo.userId, detailInfo.userName
                    ))
            }
        })
    }

    private fun setLoadingObserver() {
        stampDetailViewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) {
                ProgressDialogManager.instance.show(requireContext())
                stampDetailViewModel.postStamp()
            } else ProgressDialogManager.instance.clear()
        })
    }

    private fun setNetworkDialogObserver() {
        stampDetailViewModel.isNetworkDialogShowed.observe(viewLifecycleOwner, {
            if (it) showNetworkDialog()
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                findNavController().navigate(R.id.action_stampDetailFragment_to_homeFragment)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                stampDetailViewModel.postStamp()
                stampDetailViewModel.resetNetworkDialog()
            }
        }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        completeBtnDisposable?.dispose()
    }

    private fun setValidationObserver() {
        stampDetailViewModel.checkCommentValidEvent.observe(viewLifecycleOwner, {
            if (isCommentValid()) {
                Timber.d("success")
                stampDetailViewModel.setLoadingEvent(true)
            } else {
                Timber.d("fail")
            }
        })
    }

    private fun isCommentValid(): Boolean {
        with(binding.tvStampDetailCommentInvalid) {
            return@isCommentValid if (binding.etStampDetailComment.text.length !in 5..100) {
                isVisible = true
                requireContext().getVibrateAnimInstance().run {
                    setTarget(binding.tvStampDetailCommentInvalid)
                    start()
                }
                false
            } else {
                binding.tvStampDetailCommentInvalid.isInvisible = true
                true
            }
        }
    }
}
