package com.allcryptotokens

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Only run when we have a network connection.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            // .setRequiresCharging(true)      // <- uncomment if you want only while charging
            // .setRequiresBatteryNotLow(true) // <- optional
            .build()

        // Once a day is plenty for token descriptions.
        val request = PeriodicWorkRequestBuilder<TokenSyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "token-sync-daily",
            ExistingPeriodicWorkPolicy.KEEP, // keep the existing schedule if already enqueued
            request
        )
    }
}
