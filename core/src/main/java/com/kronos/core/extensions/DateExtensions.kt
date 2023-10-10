package com.kronos.core.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(format: String): String {
    var dateFormatted = ""
    var sdf = SimpleDateFormat(format)
    try {
        dateFormatted = sdf.format(this)
    } catch (e: Exception) {
    }
    return dateFormatted
}

fun Date.isToday(): Boolean {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    calendar.time = this

    val yearToCheck = calendar.get(Calendar.YEAR)
    val monthToCheck = calendar.get(Calendar.MONTH)
    val dayOfMonthToCheck = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.time = currentDate

    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    return (yearToCheck == currentYear && monthToCheck == currentMonth && dayOfMonthToCheck == currentDayOfMonth)

}

fun Date.of(value: String, includeHours: Boolean = false): Date? {
    var date: Date? = null
    val dateFormat = if (!includeHours) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    } else {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }

    try {
        val currentDate = dateFormat.parse(value)

        if (currentDate != null) {
            date = currentDate
        }
    } catch (e: Exception) {
        println("Exception occurred: ${e.message}")
    }
    return date
}

fun Date.getHour(): String {
    val calendar = Calendar.getInstance()
    calendar.time = this

    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    val formattedMinute = String.format("%02d", minute)

    return "$hour $amPm"
}