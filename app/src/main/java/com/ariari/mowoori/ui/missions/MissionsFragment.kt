package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.adapter.MissionsAdapter
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsFragment : BaseFragment<FragmentMissionsBinding>(R.layout.fragment_missions) {
    private val missionsViewModel: MissionsViewModel by viewModels()
    private val missionsAdapter: MissionsAdapter by lazy {
        MissionsAdapter(missionsViewModel)
    }
    private val args by navArgs<MissionsFragmentArgs>()
    private lateinit var userName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel
        missionsViewModel.setLoadingEvent(true)
        setMissionsRvAdapter()
        setObserver()
    }

    private fun setObserver() {
        setLoadingObserver()
        setPlusBtnClickObserver()
        setMissionsTypeObserver()
        setMissionsListObserver()
        setItemClickObserver()
        setUserNameObserver()
    }

    private fun setLoadingObserver() {
        missionsViewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
    }

    private fun setMissionsRvAdapter() {
        binding.rvMissions.adapter = missionsAdapter
        missionsViewModel.sendUserToLoadMissions(args.user)
    }

    private fun setPlusBtnClickObserver() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_missionsFragment_to_missionsAddFragment)
        })
    }

    private fun setMissionsTypeObserver() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner, EventObserver {
            missionsViewModel.sendUserToLoadMissions(args.user)
        })
    }

    private fun setMissionsListObserver() {
        missionsViewModel.missionsList.observe(viewLifecycleOwner) { missionsList ->
            missionsAdapter.submitList(missionsList)
            missionsViewModel.setLoadingEvent(false)
        }
    }

    private fun setItemClickObserver() {
        missionsViewModel.itemClick.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                MissionsFragmentDirections.actionMissionsFragmentToStampsFragment(
                    missionsViewModel.user.value!!.peekContent(), it.missionId
                )
            )
        })
    }

    private fun setUserNameObserver() {
        missionsViewModel.user.observe(viewLifecycleOwner, EventObserver {
            userName = it.userInfo.nickname
        })
    }
}
