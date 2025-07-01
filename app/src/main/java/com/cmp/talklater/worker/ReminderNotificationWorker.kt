package com.cmp.talklater.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.util.AppUtils.triggerNotification

class ReminderNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "It's time to call back!"
        val notificationId = inputData.getInt("notificationId", 100)
        val number = inputData.getString("number")
        triggerNotification(
            applicationContext, title, message,
            notificationId = notificationId,
            isSnooze = true,
            info = ContactInfo(
                "",
                "$number",
                "",
                -1,
                null
            )
        )
        return Result.success()
    }

}
