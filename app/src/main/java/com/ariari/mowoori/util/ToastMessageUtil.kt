package com.ariari.mowoori.util

import android.content.Context
import android.widget.Toast

fun Context.toastMessage(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
