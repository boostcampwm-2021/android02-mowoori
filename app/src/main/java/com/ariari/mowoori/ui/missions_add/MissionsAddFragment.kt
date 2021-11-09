package com.ariari.mowoori.ui.missions_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
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
        missionsAddViewModel.getGroupId()

        setPlusBtnClickObserver()
        setBackEventObserver()
    }

    private fun setPlusBtnClickObserver() {
        missionsAddViewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }

    private fun setBackEventObserver() {
        missionsAddViewModel.isPostMission.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }

    val mission = Mission("mission74", MissionInfo("미완료 미션1", "user1", 30, 10, 211101, 211201))

//    private fun setIsCreateMissionObserver() {
//        missionsAddViewModel.isCreateMission.observe(viewLifecycleOwner, EventObserver {
//            val missionInfo = MissionInfo(,)
//            with(binding) {
//                val missionInfo = MissionInfo(etMissionsAddWhat.text.toString(), )
//            }
//        })
//    }
}
