package com.allcryptotokens

import android.app.Application

class App : Application() {
    lateinit var billingManager: BillingManager
        private set

    override fun onCreate() {
        super.onCreate()
        billingManager = BillingManager(this).also { it.start() }
    }
}
