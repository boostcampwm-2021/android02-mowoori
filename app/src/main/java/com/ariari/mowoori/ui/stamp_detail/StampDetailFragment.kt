package com.ariari.mowoori.ui.stamp_detail

import android.os.Bundle
import android.view.View
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
        }
    }

    private fun setCloseBtnClickObserver() {
        viewModel.closeBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setIsCertifyObserver() {
        viewModel.isCertify.observe(viewLifecycleOwner, EventObserver {
            binding.btnStampDetailCertify.isVisible = it
        })
    }
}
