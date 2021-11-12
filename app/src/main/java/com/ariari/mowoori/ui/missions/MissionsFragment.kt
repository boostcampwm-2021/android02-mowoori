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
    private lateinit var userName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel

        setMissionsRvAdapter()
        setObserver()
        getUserName()
    }

    private fun setObserver() {
        setPlusBtnClickObserver()
        setMissionsTypeObserver()
        setMissionsListObserver()
        setItemClickObserver()
        setUserNameObserver()
    }

    private fun setMissionsRvAdapter() {
        binding.rvMissions.adapter = missionsAdapter
        missionsViewModel.loadMissionsList()
    }

    private fun setPlusBtnClickObserver() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_missionsFragment_to_missionsAddFragment)
        })
    }

    private fun setMissionsTypeObserver() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner, EventObserver {
            missionsViewModel.loadMissionsList()
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
                    it, userName
                )
            )
        })
    }

    private fun getUserName() {
        missionsViewModel.loadUserName()
    }

    private fun setUserNameObserver() {
        missionsViewModel.userName.observe(viewLifecycleOwner, EventObserver {
            userName = it
        })
    }
}
