package org.kinloch.colin.wear.bare

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Message
import android.view.SurfaceHolder
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.app.AlarmManager
import android.preference.PreferenceManager

import android.util.Log

import java.lang.Math
import java.util.Calendar
import java.lang.ref.WeakReference

import org.kinloch.colin.wear.bare.config.ConfigActivity
import org.kinloch.colin.wear.bare.comms.MessageService

class BareFace : CanvasWatchFaceService () {
  val TAG = "bare_face"
  companion object {
    private const val MSG_UPDATE_TIME = 0
    private const val INTERACTIVE_UPDATE_RATE_MS = 10
    private val NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    private val PHI = 1.6180339887498948482
  }
  
  override fun onCreateEngine () : Engine {
    startService(Intent(this, MessageService::class.java))
    return Engine(this)
  }
  
  private class EngineHandler(reference: BareFace.Engine) : Handler() {
    private val engineRef: WeakReference<BareFace.Engine> = WeakReference(reference)
    
    override fun handleMessage(message: Message) {
      val engine = engineRef.get()
      if (engine != null) {
        when (message.what) {
          MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
        }
      }
    }
  }
  
  inner class Engine(context: Context) : CanvasWatchFaceService.Engine() {
    private val context = context
    private lateinit var calendar: Calendar
    
    private val updateTimeHandler: Handler = EngineHandler(this)
    
    var textSize = 32f
    
    private lateinit var bitmap: Bitmap
    
    private var blitPaint = Paint().apply {
      isAntiAlias = false
    }
    private var forePaint = Paint().apply {
      color = getResources().getColor(R.color.foreground, getTheme())
      typeface = NORMAL_TYPEFACE
      isAntiAlias = true
      textAlign = Paint.Align.CENTER
      textSize = 64f
    }
    private val backPaint = Paint().apply {
      color = getResources().getColor(R.color.background, getTheme())
    }
    
    
    override fun onDraw(canvas: Canvas, bounds: Rect) {
      val now = System.currentTimeMillis()
      calendar.timeInMillis = now
      
      val width = bitmap.getWidth()
      val height = bitmap.getHeight()
      val frame = Rect(0, 0, width, height)
      
      val dframe = bounds
      val canvasb = Canvas(bitmap)
      
      val s = now.toDouble() / 1e3f
      val n = s / 2f
      val cD = n / 4f
      val scale = (1f + (Math.sin(n / 4f).toFloat() / 2f).toFloat())
      val rot = 20f * Math.sin(n / 2f).toFloat()
      fun toCol(v: Double) : Int { return ((1f + v) * (255f / 2f)).toInt() }
      val foreColor = Color.argb(255,
        toCol(Math.sin(cD)),
        toCol(Math.cos(cD * PHI)),
        toCol(Math.sin(cD * 7f)))
      
      //Color.argb(1f, 1.1f + Math.sin(s).toFloat() / 2.2f, 1.1f + Math.cos(s).toFloat() / 2.2f, 1.1f + Math.sin(s / 10f).toFloat() / 2.2f)
      forePaint.setColor(foreColor)
      
      val timeString = String.format("%02d:%02d:%02d.%03d", calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND))
      
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      val alpha = prefs.getString("background_alpha", "").toDoubleOrNull() ?: 0.8
      val sscale = prefs.getString("screen_scale", "").toIntOrNull() ?: 2
      
      val backColor = Color.argb((alpha * 255f).toInt(), 0, 0, 0)
      backPaint.setColor(backColor)
      
      canvasb.drawRect(frame, backPaint)
      
      canvasb.save()
      canvasb.translate(frame.right / 2f, frame.bottom / 2f)
      // canvasb.scale(1f / sscale.toFloat(), 1f / sscale.toFloat())
      canvasb.rotate(rot)
      canvasb.translate(25 * Math.sin(n * 3f).toFloat(), 25 * Math.cos(n * 5f).toFloat())
      canvasb.scale(scale / sscale, scale / sscale)
      //canvas.drawRect(Rect(-50, -50, 50, 50), forePaint)
      canvasb.drawText(timeString, 0f, 0f, forePaint)
      canvasb.restore()
      canvas.save()
      canvas.drawBitmap(bitmap,
        frame,
        dframe,
        blitPaint)
      canvas.restore()
    }
    
    override fun onCreate(surfaceHolder: SurfaceHolder) {
      calendar = Calendar.getInstance()
      
      // Move this to own service
      //AlarmManager.setWindow(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, 60000)
      // done
      super.onCreate(surfaceHolder)
    }
    override fun onDestroy() {
      updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
      super.onDestroy()
    }
    
    override fun onVisibilityChanged(visible: Boolean) {
      Log.v(TAG, if (isVisible()) "visible" else "hidden")
      
      
      if (isVisible()) {
        invalidate()
        updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
      }
      
      super.onVisibilityChanged(visible)
    }
    override fun onSurfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
      Log.v(TAG, "chang")
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      val scale = prefs.getString("screen_scale", "").toIntOrNull() ?: 2
      //forePaint.setTextSize(64f / scale.toFloat())
      bitmap = Bitmap.createBitmap(width / scale, height / scale, Bitmap.Config.RGB_565)
      super.onSurfaceChanged(surfaceHolder, format, width, height)
    }
    override fun onSurfaceCreated(surfaceHolder: SurfaceHolder) {
      Log.v(TAG, "creat")
      
      super.onSurfaceCreated(surfaceHolder)
    }
    
    private fun updateTimer() {
      updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
    }
    
    fun handleUpdateTimeMessage() {
      if (isVisible()) {
        invalidate()
      }
      val timeMs = System.currentTimeMillis()
      val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
      updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
      //if (shouldTimerBeRunning()) {
      //  Log.v(TAG, "should be running, yes")
      //}
    }
    override public fun onAmbientModeChanged(inAmbientMode: Boolean) {
      Log.v(TAG, if (inAmbientMode) "ambient" else "not ambient")
    }
  }
}
