package com.ariari.mowoori.ui.stamp_detail

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.widget.PictureDialogFragment

class StampDetailFragment :
    BaseFragment<FragmentStampDetailBinding>(R.layout.fragment_stamp_detail) {

    private val safeArgs: StampDetailFragmentArgs by navArgs()
    private val viewModel: StampDetailViewModel by viewModels()
    private lateinit var detailInfo: DetailInfo

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
        setBtnVisible()
        setDetailTransitionName()
        setUserName()
        setMissionName()
        setPicture()
        setPictureListener()
        setRootClick()
        setCloseBtnClickObserver()
        setIsCertifyObserver()
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

    private fun setMissionName() {
        viewModel.setMissionName(detailInfo.missionName)
    }

    private fun setPicture() {
        if (detailInfo.stampInfo.pictureUrl != "") {
            binding.ivStampDetail.setImageResource(R.drawable.ic_launcher_background)
            binding.tvStampDetailIcon.isInvisible = true
        }
    }

    private fun setPictureListener() {
        binding.tvStampDetailIcon.setOnClickListener {
            // TODO: 리스너 등록
            PictureDialogFragment().show(requireActivity().supportFragmentManager,
                "PictureDialogFragment")
        }
    }

    private fun setRootClick() {
        binding.container.setOnClickListener {
            hideKeyboard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }

    private fun hideKeyboard(v: View) {
        // InputMethodManager 를 통해 가상 키보드를 숨길 수 있다.
        // 현재 focus 되어있는 뷰의 windowToken 을 hideSoftInputFromWindow 메서드의 매개변수로 넘겨준다.
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
            if (it) binding.btnStampDetailCertify.isVisible = true
            else binding.btnStampDetailCertify.isInvisible = true
        })
    }
}
