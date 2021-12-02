package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogDatePickerBinding
import com.ariari.mowoori.util.getIntFormatDate
import com.ariari.mowoori.util.getIntFormatMonth
import com.ariari.mowoori.util.getIntFormatYear
import java.util.*

class DatePickerDialogFragment(
    private val isStart: Boolean,
    private val curDate: Int,
    private val startDate: Int,
    private val listener: NoticeDialogListener
) :
    BaseDialogFragment<DialogDatePickerBinding>(R.layout.dialog_date_picker) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDatePicker()
        setSaveBtnListener()
        setCancelBtnListener()
    }

    private fun setDatePicker() {
        binding.datePickerMissionDate.apply {
            minDate = if (isStart) {
                Date().time
            } else {
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, getIntFormatYear(startDate))
                    set(Calendar.MONTH, (getIntFormatMonth(startDate) - 1) % 13)
                    set(Calendar.DAY_OF_MONTH, getIntFormatDate(startDate))
                }.time.time
            }
        }.init(
            getIntFormatYear(curDate),
            (getIntFormatMonth(curDate) - 1) % 13,
            getIntFormatDate(curDate)
        ) { view, year, month, date ->
            Toast.makeText(
                view.context,
                "${year}년 ${(month + 1) % 13}월 ${date}일",
                Toast.LENGTH_SHORT
            ).show()
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
