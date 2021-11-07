package com.ariari.mowoori.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ariari.mowoori.R
import com.ariari.mowoori.util.TimberUtil

class ProgressDialogManager {
    private var progressDialog: ProgressDialog? = null

    @Synchronized
    fun show(context: Context) {
        try {
            if (progressDialog != null) {
                progressDialog?.dismiss()
            }
            progressDialog = ProgressDialog(context)
            progressDialog?.show()
        } catch (e: Exception) {
            TimberUtil.timber("ProgressDialogManager","clear: $e")
        }
    }

    @Synchronized
    fun clear() {
        try {
            if (progressDialog != null) {
                progressDialog?.dismiss()
                progressDialog = null
            }
        } catch (e: Exception) {
            TimberUtil.timber("ProgressDialogManager","clear: $e")
        }
    }

    inner class ProgressDialog(context: Context) : Dialog(context,R.style.DialogTheme ) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_progress)
            setCancelable(false)
        }
    }

    companion object {
        val instance = ProgressDialogManager()
    }
}
