package com.ariari.mowoori.ui.missions_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding
import com.ariari.mowoori.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsAddFragment :
    BaseFragment<FragmentMissionsAddBinding>(R.layout.fragment_missions_add) {
    private val missionsAddViewModel: MissionsAddViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsAddViewModel
        setPlusBtnClickObserver()
    }

    private fun setPlusBtnClickObserver() {
        missionsAddViewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }
}
