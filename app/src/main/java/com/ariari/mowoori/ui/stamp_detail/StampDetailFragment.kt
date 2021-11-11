package com.ariari.mowoori.ui.stamp_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampDetailBinding
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.EventObserver

class StampDetailFragment :
    BaseFragment<FragmentStampDetailBinding>(R.layout.fragment_stamp_detail) {

    private val safeArgs: StampDetailFragmentArgs by navArgs()
    private val viewModel: StampDetailViewModel by viewModels()
    private lateinit var stampInfo: StampInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setStampInfo()
        setCloseBtnClickObserver()
        setDetailTransitionName()
        setDetailText()
        setDetailImageView()
    }

    private fun setStampInfo() {
        stampInfo = safeArgs.stampInfo
    }

    private fun setCloseBtnClickObserver() {
        viewModel.closeBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setDetailTransitionName() {
        binding.ivStampDetail.transitionName = stampInfo.pictureUrl
    }

    private fun setDetailText() {
        binding.tvStampDetail.text = safeArgs.stampInfo.comment
    }

    private fun setDetailImageView() {
        if (stampInfo.pictureUrl != "") {
            binding.ivStampDetail.setImageResource(R.drawable.ic_launcher_background)
        }
    }
}
