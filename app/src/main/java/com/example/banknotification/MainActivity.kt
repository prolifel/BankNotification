package com.example.banknotification

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.banknotification.ui.theme.BankNotificationTheme

class MainActivity : ComponentActivity() {
  private var wasTargetAppOpen = false
  private var targetPackage = "com.instagram.android"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    checkUsageAccessPermission()

    val intent = Intent(this, AppMonitorService::class.java)
    startForegroundService(intent)
  }

  private fun checkUsageAccessPermission() {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      android.os.Process.myUid(),
      packageName
    )

    if (mode != AppOpsManager.MODE_ALLOWED) {
      startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
      Toast.makeText(this, "Please allow usage access", Toast.LENGTH_LONG).show()
    }
  }
}