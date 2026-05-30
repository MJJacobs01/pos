package com.refresh.pos.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun getCurrentTime(): String {
        return dateTimeFormat.format(Date())
    }

    fun getSQLDateFormat(calendar: Calendar): String {
        val formatted = dateTimeFormat.format(calendar.time)
        return formatted.substring(0, 10)
    }
}
