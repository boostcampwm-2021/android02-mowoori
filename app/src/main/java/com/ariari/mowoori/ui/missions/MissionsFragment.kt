package com.ariari.mowoori.ui.missions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsBinding
import com.ariari.mowoori.ui.missions.adapter.MissionsAdapter
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage
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
        setErrorMessageObserver()
    }

    private fun setLoadingObserver() {
        missionsViewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver { isLoading ->
            if (isLoading) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
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
        missionsViewModel.missionsType.observe(viewLifecycleOwner) {
            missionsViewModel.sendUserToLoadMissions(args.user)
        }
    }

    private fun setMissionsListObserver() {
        missionsViewModel.missionsList.observe(viewLifecycleOwner, EventObserver { missionsList ->
            missionsAdapter.submitList(missionsList)
            binding.tvMissionsEmpty.isVisible = missionsList.isEmpty()
            missionsViewModel.setLoadingEvent(false)
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

    private fun setUserNameObserver() {
        missionsViewModel.user.observe(viewLifecycleOwner, EventObserver {
            userName = it.userInfo.nickname
        })
    }

    private fun setErrorMessageObserver() {
        missionsViewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                "loadMissionList" -> {
                    toastMessage("에러가 발생했습니다. 잠시 후 다시 시도해주세요.")
                }
                "getUser" -> {
                    toastMessage("사용자 정보를 가져올 수 없습니다. 잠시 후 다시 시도해주세요.")
                }
                else -> {
                    toastMessage("에러가 발생했습니다. 잠시 후 다시 시도해주세요.")
                }
            }
        })
    }
}
