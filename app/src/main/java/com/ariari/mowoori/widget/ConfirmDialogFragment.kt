package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogConfirmBinding

class ConfirmDialogFragment(private val listener: NoticeDialogListener) :
    BaseDialogFragment<DialogConfirmBinding>(R.layout.dialog_confirm) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setYesListener()
        setNoListener()
    }

    private fun setYesListener() {
        binding.btnConfirmYes.setOnClickListener {
            listener.onDialogPositiveClick(this)
        }
    }

    private fun setNoListener() {
        binding.btnConfirmNo.setOnClickListener {
            listener.onDialogNegativeClick(this)
        }
    }
}
