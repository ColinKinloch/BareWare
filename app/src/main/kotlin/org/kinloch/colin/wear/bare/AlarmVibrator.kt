package org.kinloch.colin.wear.bare

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import android.preference.PreferenceManager

import android.util.Log

import java.util.Calendar

class AlarmVibrator : BroadcastReceiver() {
  val calendar = Calendar.getInstance()
  
  val TAG = "bare_face"
  
  override fun onReceive(context: Context, intent: Intent) {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")
    wl.acquire()
    setNextAlarm(context)
    alarm(context)
    wl.release()
    
  }
  
  private fun setNextAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = createPendingIntent(context)
    
    // Current time
    calendar.setTimeInMillis(System.currentTimeMillis())
    
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val interval = prefs.getString("alarm_interval", "").toIntOrNull() ?: 60
    val timeWindow = prefs.getString("alarm_window", "").toLongOrNull() ?: 30
    
    // Add 1 hour
    //calendar.add(Calendar.HOUR_OF_DAY, 1)
    calendar.add(Calendar.MINUTE, interval)
    // WARNING: This is probably bad
    calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) / interval) * interval)
    
    // HH:00:00
    //calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    
    // Use repeat but monitor margin
    // WARNING: If this fails the alarm stops
    // Second low priority alarm to reset alarm
    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * timeWindow, pendingIntent)
    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
    //  1000 * 60 * 60, pendingIntent)
  }
  
  private fun alarm(context: Context) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val vibLength = prefs.getString("alarm_length", "").toLongOrNull() ?: 400
    val vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      vib.vibrate(VibrationEffect.createOneShot(vibLength,VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
      vib.vibrate(vibLength)
    }
    Log.v(TAG, "Alarm Triggered for $vibLength")
  }
  
  private fun createPendingIntent(context: Context) : PendingIntent {
    return PendingIntent.getBroadcast(context, 0,
      Intent(context, AlarmVibrator::class.java), 0)
  }
  
  public fun setAlarm(context: Context) {
    setNextAlarm(context)
    Log.v(TAG, "Alarm Set")
  }
  public fun cancelAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = createPendingIntent(context)
    alarmManager.cancel(pendingIntent)
    Log.v(TAG, "Alarm Cancelled")
  }
}
