package com.ariari.mowoori.ui.missions_add

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.missions.entity.MissionInfo
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.widget.BaseDialogFragment
import com.ariari.mowoori.widget.DatePickerDialogFragment
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

        setObserver()
        setClickListener()
    }

    private fun setObserver() {
        setBackBtnClickObserver()
        setCompleteEventObserver()
        setCountEventObserver()
        setValidationObserver()
    }

    private fun setClickListener() {
        setRootClick()
        setButtonListener()
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
            NumberPickerDialogFragment(
                missionsAddViewModel.missionCount.value!!,
                object : BaseDialogFragment.NoticeDialogListener {
                    override fun onDialogPositiveClick(dialog: DialogFragment) {
                        val np =
                            (dialog as NumberPickerDialogFragment).binding.numberPickerMissionCount
                        missionsAddViewModel.updateMissionCount(np.value)
                        dialog.dismiss()
                    }

                    override fun onDialogNegativeClick(dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                }).show(
                requireActivity().supportFragmentManager,
                "NumberPickerFragment"
            )
        })
    }

    private fun setButtonListener() {
        binding.tvMissionsAddWhenStart.setOnClickListener {
            DatePickerDialogFragment(missionsAddViewModel.missionStartDate.value!!,
                object : BaseDialogFragment.NoticeDialogListener {
                    override fun onDialogPositiveClick(dialog: DialogFragment) {
                        val dp = (dialog as DatePickerDialogFragment).binding.datePickerMissionDate
                        missionsAddViewModel.updateMissionStartDate(
                            dp.year,
                            dp.month,
                            dp.dayOfMonth
                        )
                        dialog.dismiss()
                    }

                    override fun onDialogNegativeClick(dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                })
                .show(
                    requireActivity().supportFragmentManager,
                    "StartDatePickerFragment"
                )
        }
        binding.tvMissionsAddWhenEnd.setOnClickListener {
            DatePickerDialogFragment(
                missionsAddViewModel.missionEndDate.value!!,
                object : BaseDialogFragment.NoticeDialogListener {
                    override fun onDialogPositiveClick(dialog: DialogFragment) {
                        val dp = (dialog as DatePickerDialogFragment).binding.datePickerMissionDate
                        missionsAddViewModel.updateMissionEndDate(dp.year, dp.month, dp.dayOfMonth)
                        dialog.dismiss()
                    }

                    override fun onDialogNegativeClick(dialog: DialogFragment) {
                        dialog.dismiss()
                    }
                }).show(
                requireActivity().supportFragmentManager,
                "EndDatePickerFragment"
            )
        }
    }

    private fun setValidationObserver() {
        missionsAddViewModel.inValidMissionNameEvent.observe(viewLifecycleOwner, {
            with(binding.tvMissionsAddWhatInvalid) {
                if (binding.etMissionsAddWhat.text.length !in 1..10) {
                    isVisible = true
                    getVibrateAnimInstance().run {
                        setTarget(binding.tvMissionsAddWhatInvalid)
                        start()
                    }
                } else {
                    binding.tvMissionsAddWhatInvalid.isInvisible = true
                }
            }
        })
        missionsAddViewModel.inValidMissionDateEvent.observe(
            viewLifecycleOwner,
            EventObserver { flag ->
                with(binding.tvMissionsAddWhenInvalid) {
                    if (!flag) {
                        isVisible = true
                        getVibrateAnimInstance().run {
                            setTarget(binding.tvMissionsAddWhenInvalid)
                            start()
                        }
                    } else {
                        isInvisible = true
                    }
                }
            })
    }

    private fun getVibrateAnimInstance(): Animator {
        return AnimatorInflater.loadAnimator(requireContext(), R.animator.animator_invalid_vibrate)
    }
}

