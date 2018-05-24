package org.kinloch.colin.wear.bare.gfx

import android.service.wallpaper.WallpaperService
import org.kinloch.colin.wear.bare.*

import android.view.Surface
import android.view.SurfaceHolder
import android.os.Bundle

import android.util.Log

typealias Id = Short

class GfxFace : WallpaperService() {
  val TAG = "bare_face"
  
  override fun onCreateEngine () : Engine {
    return Engine()
  }
  
  
  init {
    System.loadLibrary("gfx_face")
  }
  
  external fun connect() : Id
  external fun disconnect(id: Id)
  external fun setPaused(id: Id, paused: Boolean)
  external fun setSurface(id: Id, surface: Surface)
  external fun destroySurface(id: Id)
  external fun setSurfaceSize(id: Id, width: Int, height: Int)
  external fun tap(id: Id)
  
  inner class Engine : WallpaperService.Engine() {
    
    var id: Short = -1
    
    override fun onCreate(surfaceHolder: SurfaceHolder) {
      super.onCreate(surfaceHolder)
      Log.v(TAG, "Connect")
      id = connect()
      Log.v(TAG, "Connected: " + id)
    }
    override fun onDestroy() {
      Log.v(TAG, "Disconnecting: " + id)
      disconnect(id)
      Log.v(TAG, "Disonnected: " + id)
      super.onDestroy()
    }
    override fun onSurfaceCreated(surfaceHolder: SurfaceHolder) {
      Log.v(TAG, "onSurfaceCreated start")
      setSurface(id, surfaceHolder.getSurface())
      Log.v(TAG, "onSurfaceCreated done")
    }
  }
}
