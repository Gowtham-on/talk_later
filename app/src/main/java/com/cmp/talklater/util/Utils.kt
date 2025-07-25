package com.cmp.talklater.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import com.cmp.talklater.model.ContactInfo
import com.cmp.talklater.model.GroupedContactInfo
import com.cmp.talklater.service.CallNotificationListenerService

object Utils {

    fun getListByGrouping(list: List<ContactInfo>): List<GroupedContactInfo> {
        return list
            .groupBy { it.number to it.type }
            .values
            .map { GroupedContactInfo(it) }
    }

    fun vibrate(context: Context, durationMs: Long = 100) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(durationMs)
            }
        }
    }

    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            pm.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    fun isNotificationServiceEnabled(context: Context): Boolean {
        val cn = ComponentName(context, CallNotificationListenerService::class.java)
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabled?.contains(cn.flattenToString()) == true
    }

    fun wakeDevice(context: Context, duration: Long = 3000L) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TalkLater:WakeLock"
        )
        wakeLock.acquire(duration)
        // The timeout passed to acquire() will automatically release the wake lock
        // after the specified duration so we don't call release() here.
    }
}
