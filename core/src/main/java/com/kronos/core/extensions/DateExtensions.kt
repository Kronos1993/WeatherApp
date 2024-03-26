package com.kronos.core.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatDate(format: String): String {
    var dateFormatted = ""
    val sdf = SimpleDateFormat(format,Locale.getDefault())
    try {
        dateFormatted = sdf.format(this)
    } catch (_: Exception) {
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

fun Date.of(value: String,includeHours: Boolean = false,timezone:String? = null): Date? {
    var date: Date? = null
    val dateFormat = if (!includeHours) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    } else {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }

    try {
        if (timezone != null) {
            dateFormat.timeZone = TimeZone.getTimeZone(timezone)
        }
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

    var hour = calendar.get(Calendar.HOUR)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "a.m." else "p.m."

    // Verificar si la hora es 0 (medianoche) y establecerla en 12 PM
    if (hour == 0 && calendar.get(Calendar.AM_PM) == Calendar.PM) {
        hour = 12
    }

    return "$hour $amPm"
}

fun Date.transformDateToTodayOrYesterday(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val date = inputFormat.parse(inputDate)

    val currentDate = Date()
    val calendar = Calendar.getInstance()
    calendar.time = currentDate

    val today = calendar.time

    calendar.add(Calendar.DATE, -1)
    val yesterday = calendar.time

    val resultToYes = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    val resultOtherDay = SimpleDateFormat("dd/MM/yy hh:mm aa", Locale.getDefault())

    return when {
        this.isSameDay(date,today) -> "today ${resultToYes.format(date)}"
        this.isSameDay(date,yesterday) -> "yesterday ${resultToYes.format(date)}"
        else -> resultOtherDay.format(date)
    }
}

fun Date.isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2

    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
           calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
           calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
}
