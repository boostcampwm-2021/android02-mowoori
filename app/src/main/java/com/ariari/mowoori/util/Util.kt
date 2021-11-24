package com.ariari.mowoori.util

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.TypedValue
import com.ariari.mowoori.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
    val today = Calendar.getInstance().time
    return dateFormat.format(today).toInt()
}

fun getCurrentDateTime(): String {
    return SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
}

fun getCurrentDatePlusMonths(month: Int): Int {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
    val calendar = Calendar.getInstance().apply {
        time = Date()
        add(Calendar.MONTH, month)
    }
    return dateFormat.format(calendar.time).toInt()
}

fun getMissionIntFormatDate(year: Int, month: Int, date: Int): Int {
    Timber.d("$year $month $date")
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
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

fun Int.px(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Int.dp(context: Context): Int {
    return (this.toFloat() / context.resources.displayMetrics.density).toInt()
}

fun Context.getVibrateAnimInstance(): Animator {
    return AnimatorInflater.loadAnimator(this, R.animator.animator_invalid_vibrate)
}
