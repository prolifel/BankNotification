package com.example.banknotification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    val channel = NotificationChannel(
      "bank_notification_channel",
      "Bank Monitoring",
      NotificationManager.IMPORTANCE_HIGH
    ).apply {
      description = "Shows when a bank app is closed"
      enableVibration(true)
      enableLights(true)
    }
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
  }
}