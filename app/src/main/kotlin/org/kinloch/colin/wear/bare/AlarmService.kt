package org.kinloch.colin.wear.bare

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager

import android.util.Log

class AlarmService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {
  val alarmVibrator = AlarmVibrator()
  
  val TAG = "bare_face"
  
  override fun onStartCommand(intent: Intent, flags: Int, startId: Int) : Int {
    Log.v(TAG, "BareAlarmService start")
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    prefs.registerOnSharedPreferenceChangeListener(this)
    handlePrefs(prefs)
    return super.onStartCommand(intent, flags, startId)
  }
  
  override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
    val prefs = preferences.getAll()
    val value = prefs.get(key)
    Log.v(TAG, "oopp he' is $key = $value")
    handlePrefs(preferences)
  }
  
  private fun handlePrefs(prefs: SharedPreferences) {
    val interval = prefs.getString("alarm_interval", "").toLongOrNull() ?: 60 
    Log.v(TAG, "interval is $interval")
    if (prefs.getBoolean("alarm_active", false)) {
      alarmVibrator.setAlarm(this)
    } else {
      alarmVibrator.cancelAlarm(this)
    }
  }
  
  override fun onBind(intent: Intent) : IBinder? {
    return null
  }
}
