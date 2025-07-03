package com.cmp.talklater.worker

import android.content.Context
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cmp.talklater.database.ContactDao
import com.cmp.talklater.util.AppUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteContactsWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val contactDao: ContactDao
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        contactDao.deleteAllContacts()
        return Result.success()
    }
}