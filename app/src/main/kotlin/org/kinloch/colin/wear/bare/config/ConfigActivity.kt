package org.kinloch.colin.wear.bare.config

import org.kinloch.colin.wear.bare.*
import org.kinloch.colin.wear.bare.comms.MessageService

import android.os.Bundle
import android.app.Activity
import android.preference.PreferenceActivity
import android.content.Intent
import android.content.Context
import android.preference.PreferenceFragment
import android.view.View
import android.util.AttributeSet

import android.util.Log

import org.kinloch.colin.wear.bare.AlarmService

class ConfigActivity : PreferenceActivity() {
  val TAG = "bare_face"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    startService(Intent(this, MessageService::class.java))
    //setContentView(R.layout.config)
    startService(Intent(this, AlarmService::class.java))
    //addPreferencesFromResource(R.xml.preferences)
  }
  override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
    loadHeadersFromResource(R.xml.preference_headers, target)
  }
  override fun isValidFragment(fragmentName: String) : Boolean {
    return true
  }
  
  class AlarmConfigFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preference_alarm)
    }
  }
  
  class OtherConfigFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preference_other)
    }
  }
}
