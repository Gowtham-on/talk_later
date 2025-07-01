package com.cmp.talklater.worker

import android.content.Context
import android.provider.CallLog
import android.util.Log
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cmp.talklater.database.ContactRepository
import com.cmp.talklater.model.ContactInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CallLogWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ContactRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val prefs = applicationContext.getSharedPreferences("call_log_prefs", Context.MODE_PRIVATE)
        val lastChecked = prefs.getLong("last_checked", 0L)

        val now = System.currentTimeMillis()
        val yesterday = now - 24 * 60 * 60 * 1000

        val uri = CallLog.Calls.CONTENT_URI
        val selection = "(${CallLog.Calls.TYPE}=? OR ${CallLog.Calls.TYPE}=?) AND ${CallLog.Calls.DATE} > ? AND ${CallLog.Calls.DATE} <= ?"
        val selectionArgs = arrayOf(
            CallLog.Calls.MISSED_TYPE.toString(),
            CallLog.Calls.REJECTED_TYPE.toString(),
            yesterday.toString(),
            now.toString()
        )

        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_PHOTO_URI
        )

        val cursor = applicationContext.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {

                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val name = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME))
                val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))

                repository.addContact(
                    ContactInfo(
                        name = name,
                        number = number,
                        time = date.toString(),
                        type = type,
                        notes = null
                    )
                )
            }
        }

        val newLastChecked = System.currentTimeMillis()
        prefs.edit { putLong("last_checked", newLastChecked) }


        return Result.success()
    }

}