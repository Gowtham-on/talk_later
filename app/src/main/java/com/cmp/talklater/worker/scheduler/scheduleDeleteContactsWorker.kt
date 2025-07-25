package com.cmp.talklater.worker.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.cmp.talklater.util.TimeUtil.getDelayToNextMidnight
import com.cmp.talklater.worker.DeleteContactsWorker
import java.util.concurrent.TimeUnit

fun scheduleDeleteContactsWorker(context: Context) {
    val delay = getDelayToNextMidnight()
    val workRequest = PeriodicWorkRequestBuilder<DeleteContactsWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "deleteContactsWorker",
        ExistingPeriodicWorkPolicy.UPDATE, // Replace or keep the old one if already scheduled
        workRequest
    )
}

fun cancelDeleteContactsWorker(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("deleteContactsWorker")
}

fun isDeleteContactsWorkerActive(context: Context, callback: (Boolean) -> Unit) {
    WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkLiveData("deleteContactsWorker")
        .observeForever { workInfoList ->
            val isActive = workInfoList.any { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> true
                    else -> false
                }
            }
            callback(isActive)
        }
}