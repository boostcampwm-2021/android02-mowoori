package com.ariari.mowoori.util

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val today = Calendar.getInstance().time
    return dateFormat.format(today).toInt()
}

fun getMissionIntFormatDate(year: Int, month: Int, date: Int): Int {
    Timber.d("$year $month $date")
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val day = Calendar.getInstance().apply {
        set(year, month, date)
    }.time
    return dateFormat.format(day).toInt()
}

fun getIntFormatYear(date: Int) = date / 10000

fun getIntFormatMonth(date: Int) = date / 100 % 100

fun getIntFormatDate(date: Int) = date % 100

fun getMissionStringFormatDate(date: Int): String {
    return "${getIntFormatYear(date)}년 ${getIntFormatMonth(date)}월 ${getIntFormatDate(date)}일"
}
