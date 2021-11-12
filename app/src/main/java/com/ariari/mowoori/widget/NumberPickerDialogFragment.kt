package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogNumberPickerBinding
import timber.log.Timber

class NumberPickerDialogFragment(private val now: Int, private val listener: NoticeDialogListener) :
    BaseDialogFragment<DialogNumberPickerBinding>(R.layout.dialog_number_picker) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNumberPicker()
        setSaveBtnListener()
        setCancelBtnListener()
    }

    private fun setNumberPicker() {
        binding.numberPickerMissionCount.apply {
            value = now
            maxValue = 30
            minValue = 10
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    private fun setSaveBtnListener() {
        binding.btnDialogNumberPickerSave.setOnClickListener {
            listener.onDialogPositiveClick(this)
        }
    }

    private fun setCancelBtnListener() {
        binding.btnDialogNumberPickerCancel.setOnClickListener {
            listener.onDialogNegativeClick(this)
        }
    }
}
