package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.adapter.MissionsAdapter
import com.ariari.mowoori.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsFragment : BaseFragment<FragmentMissionsBinding>(R.layout.fragment_missions) {
    private val missionsViewModel: MissionsViewModel by viewModels()
    private val missionsAdapter: MissionsAdapter by lazy {
        MissionsAdapter(missionsViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel
        setMissionsRvAdapter()
        setPlusBtnClickObserver()
        setMissionsTypeObserver()
        setMissionsListObserver()
        setItemClickObserver()
    }

    private fun setMissionsRvAdapter() {
        binding.rvMissions.adapter = missionsAdapter
    }

    private fun setPlusBtnClickObserver() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_missionsFragment_to_missionsAddFragment)
        })
    }

    private fun setMissionsTypeObserver() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner, EventObserver {
            missionsViewModel.setMissionsList()
        })
    }

    private fun setMissionsListObserver() {
        missionsViewModel.missionsList.observe(viewLifecycleOwner) { missionsList ->
            missionsAdapter.submitList(missionsList)
        }
    }

    private fun setItemClickObserver() {
        missionsViewModel.itemClick.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                MissionsFragmentDirections.actionMissionsFragmentToStampsFragment(
                    it
                )
            )
        })
    }
}
