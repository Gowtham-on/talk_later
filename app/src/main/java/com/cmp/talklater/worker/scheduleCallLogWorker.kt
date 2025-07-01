package com.cmp.talklater.worker

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleCallLogWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<CallLogWorker>(
        15, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "call_log_worker",
        androidx.work.ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}