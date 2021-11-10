package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogDatePickerBinding
import com.ariari.mowoori.util.getCurrentDate
import com.ariari.mowoori.util.getIntFormatDate
import com.ariari.mowoori.util.getIntFormatMonth
import com.ariari.mowoori.util.getIntFormatYear

class DatePickerDialogFragment(private val now: Int, private val listener: NoticeDialogListener) :
    BaseDialogFragment<DialogDatePickerBinding>(R.layout.dialog_date_picker) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDatePicker()
        setSaveBtnListener()
        setCancelBtnListener()
    }

    private fun setDatePicker() {
        binding.datePickerMissionDate.apply {
            minDate = getCurrentDate().toLong()
        }.init(
            getIntFormatYear(now), (getIntFormatMonth(now)-1)%13, getIntFormatDate(now)
        ) { view, year, month, date ->
            // Toast.makeText(view.context, "${year}년 ${(month+1)%13}월 ${date}일", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSaveBtnListener() {
        binding.btnDialogDatePickerSave.setOnClickListener {
            listener.onDialogPositiveClick(this)
        }
    }

    private fun setCancelBtnListener() {
        binding.btnDialogDatePickerCancel.setOnClickListener {
            listener.onDialogNegativeClick(this)
        }
    }
}
