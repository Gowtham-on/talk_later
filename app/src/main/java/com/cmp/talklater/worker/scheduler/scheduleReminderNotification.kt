package com.cmp.talklater.worker.scheduler

import android.content.Context
import androidx.work.*
import com.cmp.talklater.worker.ReminderNotificationWorker
import java.util.concurrent.TimeUnit

fun scheduleReminderNotification(
    context: Context,
    delayInMinutes: Int,
    title: String,
    message: String,
    notificationId: Int,
    number: String
) {
    val data = workDataOf(
        "title" to title,
        "message" to message,
        "notificationId" to notificationId,
        "number" to number
    )
    val request = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
        .setInitialDelay(delayInMinutes.toLong(), TimeUnit.MINUTES)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}
