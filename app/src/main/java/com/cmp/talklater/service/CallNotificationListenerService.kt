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
import com.cmp.talklater.util.Utils.wakeDevice
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

        if (sbn.packageName.contains("dialer", true) &&
            title.contains("missed call", true)
        ) {
            var number = when {
                title.contains("missed call from", true) ->
                    title.substringAfter("missed call from", "").trim()
                text.contains("missed call from", true) ->
                    text.substringAfter("missed call from", "").trim()
                else -> text.trim()
            }

            if (number.isBlank()) number = text.trim()

            val info = ContactInfo(
                name = number,
                number = number,
                time = System.currentTimeMillis().toString(),
                type = CallLog.Calls.MISSED_TYPE,
                notes = null
            )

            CoroutineScope(Dispatchers.IO).launch { repository.addContact(info) }

            wakeDevice(this)

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
