package com.cmp.talklater.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.cmp.talklater.AddNotesActivity
import com.cmp.talklater.R
import com.cmp.talklater.model.ContactInfo

object AppUtils {
    val reminderDurations = listOf(
        "5 minutes",
        "10 minutes",
        "15 minutes",
        "20 minutes",
        "25 minutes",
        "30 minutes",
        "35 minutes",
        "40 minutes",
        "45 minutes",
        "50 minutes",
        "55 minutes",
    )


    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
        }
        context.startActivity(intent)
    }

    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
            }
        }
        context.startActivity(intent)
    }

    const val ACTION_ANSWERED = "com.cmp.talklater.ACTION_ANSWERED"

    fun triggerNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = "reminder_channel",
        channelName: String = "Reminders",
        notificationId: Int = 1001,
        info: ContactInfo? = null,
        isSnooze: Boolean = false
    ) {

        // Create channel if needed (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        /** Notification actions can be used later */
//        val answeredIntent = Intent(context, NotificationActionReceiver::class.java).apply {
//            action = ACTION_ANSWERED
//            putExtra("notificationId", notificationId)
//        }
//        val answeredPendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            answeredIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.talk_later_icon_v2)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        if (!isSnooze) {
            if (info != null) {
                val callBackPendingIntent = getCallBackPendingIntent(context, info.number)
                builder.addAction(0, "Call Back", callBackPendingIntent)
            }
            if (info != null) {
                val addNotesPendingIntent = getNotesPendingIntent(context, info, notificationId)
                builder.addAction(0, "Remind After", addNotesPendingIntent)
            }
        } else {
            if (info != null) {
                val callBackPendingIntent = getCallBackPendingIntent(context, info.number)
                builder.addAction(0, "Call Back", callBackPendingIntent)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }


    fun getCallBackPendingIntent(context: Context, phoneNumber: String): PendingIntent {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        return PendingIntent.getActivity(
            context,
            0,
            dialIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun getNotesPendingIntent(
        context: Context,
        info: ContactInfo,
        notificationId: Int
    ): PendingIntent {
        val addNotesIntent = Intent(context, AddNotesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("notificationId", notificationId)
            putExtra("number", info.number)
            putExtra("notes", info.notes)
            putExtra("name", info.name)
            putExtra("type", info.type)
            putExtra("time", info.time)
        }

        return PendingIntent.getActivity(
            context,
            1,
            addNotesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}