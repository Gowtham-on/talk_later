package com.cmp.talklater.util

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

}