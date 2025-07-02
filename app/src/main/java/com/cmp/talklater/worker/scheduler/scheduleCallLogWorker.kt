package com.cmp.talklater.worker.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cmp.talklater.worker.CallLogWorker
import java.util.concurrent.TimeUnit

fun scheduleCallLogWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<CallLogWorker>(
        15, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "call_log_worker",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}