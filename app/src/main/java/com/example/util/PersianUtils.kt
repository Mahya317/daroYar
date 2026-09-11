package com.example.util

import java.text.SimpleDateFormat
import java.util.*

object PersianUtils {
    fun toPersianDigits(text: String): String {
        val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var result = text
        for (i in 0..9) {
            result = result.replace(i.toString(), persianNumbers[i])
        }
        return result
    }

    fun getPersianDayName(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateString) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = date
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> "شنبه"
                Calendar.SUNDAY -> "یکشنبه"
                Calendar.MONDAY -> "دوشنبه"
                Calendar.TUESDAY -> "سه‌شنبه"
                Calendar.WEDNESDAY -> "چهارشنبه"
                Calendar.THURSDAY -> "پنج‌شنبه"
                Calendar.FRIDAY -> "جمعه"
                else -> "امروز"
            }
        } catch (e: Exception) {
            "امروز"
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
