package com.ariari.mowoori.util

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val today = Calendar.getInstance().time
    return dateFormat.format(today).toInt()
}

fun getCurrentDatePlusMonths(month: Int): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val calendar = Calendar.getInstance().apply {
        time = Date()
        add(Calendar.MONTH, month)
    }
    return dateFormat.format(calendar.time).toInt()
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

// %2d가 안되는 이유 찾기
fun getMissionStringFormatDate(date: Int): String {
    return String.format(
        "%d년 %02d월 %02d일",
        getIntFormatYear(date),
        getIntFormatMonth(date),
        getIntFormatDate(date)
    )
}
