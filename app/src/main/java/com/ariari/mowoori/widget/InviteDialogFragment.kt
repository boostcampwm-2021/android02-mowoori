package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogInviteBinding

class InviteDialogFragment(
    private val inviteCode: String,
    private val listener: InviteDialogListener
) :
    BaseDialogFragment<DialogInviteBinding>(R.layout.dialog_invite) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInviteCode()
        setYesListener()
        setNoListener()
        setCopyListener()
    }

    private fun setInviteCode() {
        binding.tvInviteCode.text = inviteCode
    }

    private fun setYesListener() {
        binding.btnConfirmYes.setOnClickListener {
            listener.onPositiveClick(this)
        }
    }

    private fun setNoListener() {
        binding.btnConfirmNo.setOnClickListener {
            listener.onNegativeClick(this)
        }
    }

    private fun setCopyListener() {
        binding.btnConfirmNo.setOnClickListener {
            listener.onCopyClick(this, inviteCode)
        }
    }

    interface InviteDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
        fun onNegativeClick(dialog: DialogFragment)
        fun onCopyClick(dialog: DialogFragment, inviteCode: String)
    }

}
