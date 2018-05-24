package org.kinloch.colin.wear.bare

import android.service.wallpaper.WallpaperService

import android.os.Bundle
import android.view.SurfaceHolder
import android.content.Intent
import android.content.BroadcastReceiver

import android.util.Log

abstract class WatchFaceService : WallpaperService() {
  private val TAG = "bare_face"
  
  inner abstract class Engine : WallpaperService.Engine() {
    
    open override fun onCreate(surfaceHolder: SurfaceHolder) {
      super.onCreate(surfaceHolder)
    }
    open fun onAmbientModeChanged(inAmbientMode: Boolean) {}
  }
}
