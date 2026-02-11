package com.allcryptotokens

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Nothing here. DB update happens inside AppDatabase.get()
    }
}
