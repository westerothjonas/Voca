package com.example.voca.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class VocaReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        VocaNotificationHelper.showStudyReminder(applicationContext)
        return Result.success()
    }
}
