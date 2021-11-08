package com.ariari.mowoori.util

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val today = Calendar.getInstance().time
    return dateFormat.format(today).toInt()
}
