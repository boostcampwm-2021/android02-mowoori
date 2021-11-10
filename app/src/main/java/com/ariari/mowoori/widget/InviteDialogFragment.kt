package com.ariari.mowoori.widget

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
        binding.tvInviteCode.setOnClickListener {
            listener.onCopyClick(this, inviteCode)
        }
    }

    override fun onResume() {
        super.onResume()
        val size = getDeviceSize(requireContext())

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.first
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun getDeviceSize(context: Context): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            Pair(size.x, size.y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            Pair(rect.width(), rect.height())
        }
    }

    interface InviteDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
        fun onNegativeClick(dialog: DialogFragment)
        fun onCopyClick(dialog: DialogFragment, inviteCode: String)
    }

}
