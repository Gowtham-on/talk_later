package com.cmp.talklater.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.provider.CallLog
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

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
        if (title.contains("missed call", true)) {
            val number = text
            val info = ContactInfo(
                name = number,
                number = number,
                time = System.currentTimeMillis().toString(),
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
