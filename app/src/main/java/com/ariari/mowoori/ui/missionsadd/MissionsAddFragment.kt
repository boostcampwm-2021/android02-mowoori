package com.ariari.mowoori.ui.missionsadd

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
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.BaseDialogFragment
import com.ariari.mowoori.widget.DatePickerDialogFragment
import com.ariari.mowoori.widget.NumberPickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MissionsAddFragment :
    BaseFragment<FragmentMissionsAddBinding>(R.layout.fragment_missions_add) {
    private val missionsAddViewModel: MissionsAddViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionsAddViewModel
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
        missionsAddViewModel.isMissionPosted.observe(viewLifecycleOwner, EventObserver {
            toastMessage("미션이 성공적으로 추가되었습니다.")
            this.findNavController().navigateUp()
        })
    }

    private fun setCountEventObserver() {
        missionsAddViewModel.numberCountClick.observe(viewLifecycleOwner, EventObserver {
            NumberPickerDialogFragment(
                missionsAddViewModel.missionCount.value!!,
                object : BaseDialogFragment.NoticeDialogListener {
                    override fun onDialogPositiveClick(dialog: DialogFragment) {
                        val np =
                            (dialog as NumberPickerDialogFragment).binding.numberPickerMissionCount
                        missionsAddViewModel.updateMissionCount(
                            when (np.value) {
                                0 -> 10
                                1 -> 20
                                2 -> 30
                                else -> throw IllegalAccessException()
                            }
                        )
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

    private fun setValidationObserver() {
        missionsAddViewModel.checkMissionValidEvent.observe(viewLifecycleOwner, {
            if (isMissionNameValid() && isMissionDateValid()) {
                Timber.d("success")
                missionsAddViewModel.postMission(binding.etMissionsAddWhat.text.toString())
            } else {
                Timber.d("fail")
            }
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

    private fun isMissionNameValid(): Boolean {
        with(binding.tvMissionsAddWhatInvalid) {
            return@isMissionNameValid if (binding.etMissionsAddWhat.text.length !in 1..10) {
                isVisible = true
                getVibrateAnimInstance().run {
                    setTarget(binding.tvMissionsAddWhatInvalid)
                    start()
                }
                false
            } else {
                binding.tvMissionsAddWhatInvalid.isInvisible = true
                true
            }
        }
    }

    private fun isMissionDateValid(): Boolean {
        with(binding.tvMissionsAddWhenInvalid) {
            return@isMissionDateValid if (missionsAddViewModel.missionStartDate.value!! > missionsAddViewModel.missionEndDate.value!!) {
                isVisible = true
                getVibrateAnimInstance().run {
                    setTarget(binding.tvMissionsAddWhenInvalid)
                    start()
                }
                false
            } else {
                isInvisible = true
                true
            }
        }
    }

    private fun getVibrateAnimInstance(): Animator {
        return AnimatorInflater.loadAnimator(requireContext(), R.animator.animator_invalid_vibrate)
    }
}

