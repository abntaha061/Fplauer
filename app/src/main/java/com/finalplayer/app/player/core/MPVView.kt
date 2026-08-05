package com.finalplayer.app.player.core

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import dev.jdtech.mpv.MPVLib
import java.io.File

class MPVView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var mpvLib: MPVLib? = null
    private var isInitialized = false
    private var surfaceCreated = false

    var isPaused: Boolean = true
        private set
    var positionMs: Long = 0L
        private set
    var durationMs: Long = 0L
        private set

    init {
        holder.addCallback(this)
    }

    fun initialize(context: Context, configDir: File) {
        if (isInitialized) return
        try {
            val lib = MPVLib.create(context)
            if (lib != null) {
                mpvLib = lib
                lib.setOptionString("config", "yes")
                lib.setOptionString("config-dir", configDir.absolutePath)
                lib.setOptionString("vo", "gpu")
                lib.setOptionString("gpu-context", "android")
                lib.setOptionString("hwdec", "auto")
                lib.init()
                isInitialized = true
                Log.d("MPVView", "MPVLib initialized successfully")
            } else {
                Log.e("MPVView", "MPVLib.create returned null")
            }
        } catch (e: Throwable) {
            Log.e("MPVView", "Error initializing MPVLib", e)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceCreated = true
        if (isInitialized) {
            try {
                mpvLib?.attachSurface(holder.surface)
            } catch (e: Throwable) {
                Log.e("MPVView", "Error attaching surface", e)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceCreated = false
        if (isInitialized) {
            try {
                mpvLib?.detachSurface()
            } catch (e: Throwable) {
                Log.e("MPVView", "Error detaching surface", e)
            }
        }
    }

    fun playFile(path: String) {
        if (!isInitialized) return
        try {
            mpvLib?.command(arrayOf("loadfile", path))
            isPaused = false
        } catch (e: Throwable) {
            Log.e("MPVView", "Error playing file: $path", e)
        }
    }

    fun pause() {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyBoolean("pause", true)
            isPaused = true
        } catch (e: Throwable) {
            Log.e("MPVView", "Error pausing", e)
        }
    }

    fun resume() {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyBoolean("pause", false)
            isPaused = false
        } catch (e: Throwable) {
            Log.e("MPVView", "Error resuming", e)
        }
    }

    fun seekTo(positionMs: Long) {
        if (!isInitialized) return
        try {
            val seconds = positionMs / 1000.0
            mpvLib?.command(arrayOf("seek", seconds.toString(), "absolute"))
        } catch (e: Throwable) {
            Log.e("MPVView", "Error seeking", e)
        }
    }

    fun updatePlaybackState() {
        if (!isInitialized) return
        try {
            val posSeconds = mpvLib?.getPropertyDouble("time-pos") ?: 0.0
            val durSeconds = mpvLib?.getPropertyDouble("duration") ?: 0.0
            val paused = mpvLib?.getPropertyBoolean("pause") ?: true

            positionMs = (posSeconds * 1000).toLong().coerceAtLeast(0L)
            durationMs = (durSeconds * 1000).toLong().coerceAtLeast(0L)
            isPaused = paused
        } catch (e: Throwable) {
            // Ignore property reading errors when no file loaded
        }
    }

    fun stop() {
        if (!isInitialized) return
        try {
            mpvLib?.command(arrayOf("stop"))
            isPaused = true
            positionMs = 0L
            durationMs = 0L
        } catch (e: Throwable) {
            Log.e("MPVView", "Error stopping", e)
        }
    }

    fun destroy() {
        if (!isInitialized) return
        try {
            mpvLib?.destroy()
            isInitialized = false
            mpvLib = null
        } catch (e: Throwable) {
            Log.e("MPVView", "Error destroying MPVLib", e)
        }
    }
}
