package org.kinloch.colin.wear.bare

import android.graphics.Canvas
import android.graphics.Rect

abstract class CanvasWatchFaceService : WatchFaceService() {
  inner abstract class Engine : WatchFaceService.Engine() {
    abstract fun onDraw(canvas: Canvas, bounds: Rect)
    public fun invalidate() {
      val surface = getSurfaceHolder()
      val bounds = surface.getSurfaceFrame()
      val canvas = surface.lockCanvas(bounds)
      onDraw(canvas, bounds)
      surface.unlockCanvasAndPost(canvas)
    }
  }
}
