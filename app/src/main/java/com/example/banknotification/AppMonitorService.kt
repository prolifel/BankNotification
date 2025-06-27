package com.example.banknotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AppMonitorService : Service() {
  private var wasTargetAppOpen = false
  private val targetPackage: String
    get() {
      val prefs = getSharedPreferences("app_monitor", MODE_PRIVATE)
      return prefs.getString("target_package", "") ?: ""
    }

  override fun onCreate() {
    super.onCreate()

    startForeground(1, createServiceNotification())
    startMonitoring()
  }

  private fun createServiceNotification() : Notification {
    val channelId = "monitor_bank_channel"
    val channel = NotificationChannel(
      channelId,
      "Bank Monitor Background",
      NotificationManager.IMPORTANCE_LOW
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)

    return NotificationCompat.Builder(this, channelId)
      .setContentTitle("Bank Monitor Running")
      .setContentText("Monitoring banks app usage...")
      .setSmallIcon(android.R.drawable.ic_popup_sync)
      .build()
  }

  private fun startMonitoring() {
    Thread {
      while (true) {
        val currentApp = getForegroundApp()
        println("Current app: $currentApp")
        if (currentApp == targetPackage) {
          wasTargetAppOpen = true
        } else {
          if (wasTargetAppOpen) {
            wasTargetAppOpen = false
            showHeadsUpNotification("Bank App Closed", "$targetPackage was closed")
          }
        }
        Thread.sleep(2000) // sleep 2 sec
      }
    }.start()
  }

  private fun getForegroundApp(): String? {
    val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val now = System.currentTimeMillis()
    val stats = usm.queryUsageStats(
      UsageStatsManager.INTERVAL_DAILY,
      now - 10000,
      now
    )

    return stats.maxByOrNull { it.lastTimeUsed }?.packageName
  }

  private fun showHeadsUpNotification(title: String, message: String) {
    val channelId = "popup_notification_channel"
    val channel = NotificationChannel(
      channelId,
      "Bank Monitor Alert",
      NotificationManager.IMPORTANCE_HIGH
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(this, channelId)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_MESSAGE)
      .setAutoCancel(true)
      .setDefaults(NotificationCompat.DEFAULT_ALL)

    manager.notify(2, builder.build())
  }

  override fun onBind(p0: Intent?): IBinder? = null
}