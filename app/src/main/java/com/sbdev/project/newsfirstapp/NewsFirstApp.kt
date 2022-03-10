package com.sbdev.project.newsfirstapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NewsFirstApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}