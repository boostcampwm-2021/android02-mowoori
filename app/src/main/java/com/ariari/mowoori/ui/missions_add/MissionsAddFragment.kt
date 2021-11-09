package com.ariari.mowoori.ui.missions_add

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.widget.NumberPickerDialogFragment
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
        missionsAddViewModel.updateMissionCount(10)

        setBackBtnClickObserver()
        setCompleteEventObserver()
        setRootClick()
        setCountEventObserver()
    }

    private fun setRootClick() {
        binding.root.setOnClickListener {
            hideKeyboard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }

    private fun hideKeyboard(v: View) {
        // InputMethodManager 를 통해 가상 키보드를 숨길 수 있다.
        // 현재 focus 되어있는 뷰의 windowToken 을 hideSoftInputFromWindow 메서드의 매개변수로 넘겨준다.
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun setBackBtnClickObserver() {
        missionsAddViewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }

    private fun setCompleteEventObserver() {
        missionsAddViewModel.isPostMission.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigateUp()
        })
    }

    val mission = Mission("mission74", MissionInfo("미완료 미션1", "user1", 30, 10, 211101, 211201))

    private fun setCountEventObserver() {
        missionsAddViewModel.numberCountClick.observe(viewLifecycleOwner, EventObserver {
            NumberPickerDialogFragment(missionsAddViewModel).show(
                requireActivity().supportFragmentManager,
                "NumberPickerFragment"
            )
        })
    }
}
