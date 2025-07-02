package com.cmp.talklater.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cmp.talklater.worker.scheduler.scheduleCallLogWorker
import com.cmp.talklater.worker.scheduler.scheduleDeleteContactsWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleDeleteContactsWorker(context)
            scheduleCallLogWorker(context)
        }
    }
}