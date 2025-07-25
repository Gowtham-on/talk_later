package com.cmp.talklater.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.cmp.talklater.worker.scheduler.isDeleteContactsWorkerActive
import com.cmp.talklater.worker.scheduler.scheduleDeleteContactsWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("app_util", MODE_PRIVATE)
            val isDeleteWorkerActive = prefs.getBoolean("is_worker_active", false)
            if (isDeleteWorkerActive) {
                isDeleteContactsWorkerActive(context) {
                    scheduleDeleteContactsWorker(context)
                }
            }
        }
    }
}