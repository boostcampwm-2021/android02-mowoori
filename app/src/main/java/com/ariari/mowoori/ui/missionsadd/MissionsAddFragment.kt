package com.ariari.mowoori.ui.missionsadd

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentMissionsAddBinding
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.getVibrateAnimInstance
import com.ariari.mowoori.util.hideKeyBoard
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.util.toastMessage
import com.ariari.mowoori.widget.BaseDialogFragment
import com.ariari.mowoori.widget.DatePickerDialogFragment
import com.ariari.mowoori.widget.NetworkDialogFragment
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
        setNetworkDialogObserver()
    }

    private fun setClickListener() {
        setRootClick()
        setButtonListener()
    }

    private fun setRootClick() {
        binding.root.setOnClickListener {
            requireContext().hideKeyBoard(it)
            requireActivity().currentFocus?.clearFocus()
        }
    }

    private fun setBackBtnClickObserver() {
        missionsAddViewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setCompleteEventObserver() {
        missionsAddViewModel.isMissionPosted.observe(viewLifecycleOwner, {
            if (it) {
                toastMessage("미션이 성공적으로 추가되었습니다.")
                this.findNavController().navigate(R.id.action_missionsAddFragment_to_missionsFragment)
            }
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
                postMission()
            } else {
                Timber.d("fail")
            }
        })
    }

    private fun postMission() {
        if (requireContext().isNetWorkAvailable()) {
            missionsAddViewModel.postMission(binding.etMissionsAddWhat.text.toString())
        } else {
            showNetworkDialog()
        }
    }

    private fun setButtonListener() {
        binding.tvMissionsAddWhenStart.setOnClickListener {
            DatePickerDialogFragment(
                isStart = true,
                curDate = missionsAddViewModel.missionStartDate.value!!,
                startDate = missionsAddViewModel.missionStartDate.value!!,
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
                isStart = false,
                curDate = missionsAddViewModel.missionEndDate.value!!,
                startDate = missionsAddViewModel.missionStartDate.value!!,
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
            return@isMissionNameValid if (binding.etMissionsAddWhat.text.length !in 1..15) {
                isVisible = true
                requireContext().getVibrateAnimInstance().run {
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
                requireContext().getVibrateAnimInstance().run {
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

    private fun setNetworkDialogObserver() {
        missionsAddViewModel.isNetworkDialogShowed.observe(viewLifecycleOwner, EventObserver {
            if (it) showNetworkDialog()
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                findNavController().navigate(R.id.action_missionsAddFragment_to_homeFragment)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                postMission()
                missionsAddViewModel.resetNetworkDialog()
            }
        }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
    }
}

