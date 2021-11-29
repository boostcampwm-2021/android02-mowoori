package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.adapter.MissionsAdapter
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionsFragment : BaseFragment<FragmentMissionsBinding>(R.layout.fragment_missions) {
    private val missionsViewModel: MissionsViewModel by viewModels()
    private val missionsAdapter: MissionsAdapter by lazy {
        MissionsAdapter(missionsViewModel)
    }
    private val args by navArgs<MissionsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsViewModel
        setMissionsRvAdapter()
        setPlusButtonVisible()
        loadMissions()
        setObserver()
    }

    private fun setMissionsRvAdapter() {
        binding.rvMissions.adapter = missionsAdapter
    }

    private fun setPlusButtonVisible() {
        missionsViewModel.setIsMemberMission(args.user)
    }

    private fun loadMissions() {
        if (requireContext().isNetWorkAvailable()) {
            missionsViewModel.setLoadingEvent(true)
            missionsViewModel.loadUserThenLoadMissionList(args.user)
        } else {
            showNetworkDialog()
        }
    }

    private fun setObserver() {
        setLoadingObserver()
        setPlusBtnClickObserver()
        setBackBtnClickObserver()
        setMissionsTypeObserver()
        setMissionsListObserver()
        setItemClickObserver()
        setNetworkDialogObserver()
    }

    private fun setLoadingObserver() {
        missionsViewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
    }

    private fun setPlusBtnClickObserver() {
        missionsViewModel.plusBtnClick.observe(viewLifecycleOwner, EventObserver {
            if (missionsViewModel.isEmptyGroupList) {
                toastMessage(getString(R.string.missions_no_group))
            } else {
                this.findNavController()
                    .navigate(R.id.action_missionsFragment_to_missionsAddFragment)
            }
        })
    }

    private fun setBackBtnClickObserver() {
        missionsViewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setMissionsTypeObserver() {
        missionsViewModel.missionsType.observe(viewLifecycleOwner) {
            missionsViewModel.loadUserThenLoadMissionList(args.user)
        }
    }

    private fun setMissionsListObserver() {
        missionsViewModel.filteredMissionList.observe(viewLifecycleOwner, { missionsList ->
            missionsAdapter.submitList(missionsList)
            binding.tvMissionsEmpty.isVisible = missionsList.isEmpty()
        })
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

    private fun setNetworkDialogObserver() {
        missionsViewModel.isNetworkDialogShowed.observe(viewLifecycleOwner, EventObserver {
            if (it) showNetworkDialog()
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                findNavController().navigate(R.id.action_missionsFragment_to_homeFragment)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                loadMissions()
                missionsViewModel.resetNetworkDialog()
            }
        }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
    }
}
