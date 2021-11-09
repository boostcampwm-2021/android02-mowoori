package com.ariari.mowoori.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toastMessage(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastMessage(msg: String){
    this.requireContext().toastMessage(msg)
}
