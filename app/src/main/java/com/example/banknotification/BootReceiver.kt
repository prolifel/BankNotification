package com.example.banknotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
      Toast.makeText(context, "Your device has been rebooted. Starting monitor the bank application...", Toast.LENGTH_SHORT).show()

      val serviceIntent = Intent(context, AppMonitorService::class.java)
      context.startForegroundService(serviceIntent)
    }
  }
}