package com.cmp.talklater.service

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.provider.CallLog
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.cmp.talklater.database.ContactRepository
import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.util.AppUtils.triggerNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CallNotificationListenerService : NotificationListenerService() {
    @Inject
    lateinit var repository: ContactRepository

    val listOfNotificationId = mutableListOf<Long>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
        val isCall = sbn.notification?.channelId  == "phone_missed_call"
        Log.d("NotificationListenerLog", "onNotificationPosted: $title $text $isCall ${sbn.postTime}" )
        if (title.contains("missed call", true) && isCall && !listOfNotificationId.contains(sbn.postTime)) {
            listOfNotificationId.add(sbn.postTime)
            val number = text
            val info = ContactInfo(
                name = number,
                number = number,
                time = sbn.notification.`when`.toString(),
                type = CallLog.Calls.MISSED_TYPE,
                notes = null
            )
            CoroutineScope(Dispatchers.IO).launch { repository.addContact(info) }
            triggerNotification(
                context = this,
                title = "Missed call from $number",
                message = number,
                notificationId = number.hashCode(),
                info = info
            )
        }
    }
}
