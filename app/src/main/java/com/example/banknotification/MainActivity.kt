package com.example.banknotification

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
  private lateinit var appSpiner: Spinner
  private lateinit var btnStart: Button
  private var appList = mutableListOf<String>()
  private var selectedPackage = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    appSpiner = findViewById(R.id.appSpinner)
    btnStart = findViewById(R.id.btnStartMonitoring)

    checkUsageAccessPermission()
    loadInstalledApps()

    btnStart.setOnClickListener {
      selectedPackage = appList[appSpiner.selectedItemPosition]
      val prefs = getSharedPreferences("app_monitor", MODE_PRIVATE)
      prefs.edit { putString("target_package", selectedPackage) }

      Toast.makeText(this, "Will monitoring: $selectedPackage", Toast.LENGTH_SHORT).show()

      val intent = Intent(this, AppMonitorService::class.java)
      startForegroundService(intent)
    }
  }

  private fun loadInstalledApps() {
    val pm = packageManager
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    appList = packages.filter { pm.getLaunchIntentForPackage(it.packageName) != null }
      .map { it.packageName }
      .toMutableList()

    val displayNames = packages.filter { pm.getLaunchIntentForPackage(it.packageName) != null }
      .map { pm.getApplicationLabel(it).toString() }

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, displayNames)
    appSpiner.adapter = adapter
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