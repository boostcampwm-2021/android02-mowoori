package com.ariari.mowoori.widget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.DialogNetworkBinding

class NetworkDialogFragment(private val listener: NetworkDialogListener): BaseDialogFragment<DialogNetworkBinding>(R.layout.dialog_network) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCancelListener()
        setRetryListener()
    }

    private fun setCancelListener() {
        binding.btnDialogNetworkCancel.setOnClickListener { listener.onCancelClick(this) }
    }

    private fun setRetryListener() {
        binding.btnDialogNetworkRetry.setOnClickListener { listener.onRetryClick(this) }
    }

    interface NetworkDialogListener {
        fun onCancelClick(dialog: DialogFragment)
        fun onRetryClick(dialog: DialogFragment)
    }
}
