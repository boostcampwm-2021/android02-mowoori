package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogNumberPickerBinding
import com.ariari.mowoori.ui.missions_add.MissionsAddViewModel
import timber.log.Timber

class NumberPickerDialogFragment(
    private val missionsAddViewModel: MissionsAddViewModel
) :
    BaseDialogFragment<DialogNumberPickerBinding>(R.layout.dialog_number_picker) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNumberPicker()
        setSaveBtnListener()
        setCancelBtnListener()
    }

    private fun setNumberPicker() {
        binding.numberPickerMissionCount.apply {
            maxValue = 30
            minValue = 10
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    private fun setSaveBtnListener() {
        binding.btnDialogNumberPickerSave.setOnClickListener {
            Timber.d(binding.numberPickerMissionCount.value.toString())
            missionsAddViewModel.updateMissionCount(binding.numberPickerMissionCount.value)
            dismiss()
        }
    }

    private fun setCancelBtnListener() {
        binding.btnDialogNumberPickerCancel.setOnClickListener {
            dismiss()
        }
    }
}
