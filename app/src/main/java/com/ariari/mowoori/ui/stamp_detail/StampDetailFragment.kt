package com.ariari.mowoori.ui.stamp_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setStampInfo()
        setCloseBtnClickObserver()
        binding.tvStampDetail.text = safeArgs.stampInfo.comment
    }

    private fun setStampInfo() {
        stampInfo = safeArgs.stampInfo
    }

    private fun setCloseBtnClickObserver() {
        viewModel.closeBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }
}
