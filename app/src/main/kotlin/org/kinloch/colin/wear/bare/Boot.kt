package org.kinloch.colin.wear.bare

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.util.Log

class Boot : BroadcastReceiver() {
  val TAG = "bare_face"
  override fun onReceive(context: Context, intent: Intent) {
    Log.v(TAG, "Boot ready")
    context.startService(Intent(context, AlarmService::class.java))
  }
}
