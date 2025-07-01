package com.cmp.talklater.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.cmp.talklater.util.AppUtils.ACTION_ANSWERED

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ANSWERED -> {
                val notificationId = intent.getIntExtra("notificationId", -1)
                NotificationManagerCompat.from(context).cancel(notificationId)
                Toast.makeText(context, "Pressed answered", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
