package com.cmp.talklater.util

import java.util.Calendar

object TimeUtil {

    fun formatMillis(millis: Long, format: String = "dd/MM/yyyy HH:mm"): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Use java.time for Android O and above
            val formatter = java.time.format.DateTimeFormatter.ofPattern(format)
            val instant = java.time.Instant.ofEpochMilli(millis)
            val zoneId = java.time.ZoneId.systemDefault()
            java.time.LocalDateTime.ofInstant(instant, zoneId).format(formatter)
        } else {
            // Use SimpleDateFormat for older versions
            val sdf = java.text.SimpleDateFormat(format, java.util.Locale.getDefault())
            val date = java.util.Date(millis)
            sdf.format(date)
        }
    }


    fun getDelayToNextMidnight(): Long {
        val now = Calendar.getInstance()
        val midnight = now.clone() as Calendar
        midnight.add(Calendar.DAY_OF_YEAR, 1)
        midnight.set(Calendar.HOUR_OF_DAY, 0)
        midnight.set(Calendar.MINUTE, 0)
        midnight.set(Calendar.SECOND, 0)
        midnight.set(Calendar.MILLISECOND, 0)
        return midnight.timeInMillis - now.timeInMillis
    }

}