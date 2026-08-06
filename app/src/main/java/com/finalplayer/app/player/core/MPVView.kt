package com.finalplayer.app.player.core

import android.content.Context
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import dev.jdtech.mpv.MPVLib
import com.finalplayer.app.ui.player.controls.components.sheets.TrackNode
import com.finalplayer.app.ui.player.controls.components.sheets.ChapterNode
import java.io.File

class MPVView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var mpvLib: MPVLib? = null
    var isInitialized = false
        private set

    var isSurfaceReady = false
        private set

    var onSurfaceReady: (() -> Unit)? = null

    // Playback state properties
    var isPaused: Boolean = true
        private set
    var positionMs: Long = 0L
        private set
    var durationMs: Long = 0L
        private set
    var isPausedForCache: Boolean = false
        private set
    var cacheTimeSeconds: Double = 0.0
        private set
    var isEofReached: Boolean = false
        private set
    var videoAspect: Double = 1.7777777777777777 // 16:9 default
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
                initOptions(configDir)
                lib.init()
                isInitialized = true
                Log.d(TAG, "MPVLib initialized successfully")

                if (isSurfaceReady) {
                    lib.attachSurface(holder.surface)
                }
            } else {
                Log.e(TAG, "MPVLib.create returned null")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error initializing MPVLib", e)
        }
    }

    private fun initOptions(configDir: File) {
        val lib = mpvLib ?: return
        try {
            lib.setOptionString("config", "yes")
            lib.setOptionString("config-dir", configDir.absolutePath)

            // Hardware decoding setup
            lib.setOptionString("hwdec", "mediacodec,mediacodec-copy,no")
            lib.setOptionString("hwdec-codecs", "all")

            // Video output setup
            lib.setOptionString("vo", "gpu")
            lib.setOptionString("gpu-context", "android")

            // Playback and UI settings
            lib.setOptionString("keep-open", "yes")
            lib.setOptionString("input-default-bindings", "yes")
            lib.setOptionString("hr-seek", "yes")
            lib.setOptionString("hr-seek-framedrop", "yes")
            lib.setOptionString("video-sync", "audio")
            lib.setOptionString("framedrop", "vo")

            // Screenshot directory setup
            val screenshotDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: File(context.filesDir, "screenshots")
            if (!screenshotDir.exists()) screenshotDir.mkdirs()
            lib.setOptionString("screenshot-directory", screenshotDir.absolutePath)

            applySubtitleOptions()

            Log.d(TAG, "MPV options initialized successfully")
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting MPV options", e)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceReady = true
        if (isInitialized) {
            try {
                mpvLib?.attachSurface(holder.surface)
            } catch (e: Throwable) {
                Log.e(TAG, "Error attaching surface", e)
            }
        }
        onSurfaceReady?.invoke()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceReady = false
        if (isInitialized) {
            try {
                mpvLib?.detachSurface()
            } catch (e: Throwable) {
                Log.e(TAG, "Error detaching surface", e)
            }
        }
    }

    fun playFile(path: String) {
        if (!isInitialized) return
        try {
            mpvLib?.command(arrayOf("loadfile", path))
            isPaused = false
        } catch (e: Throwable) {
            Log.e(TAG, "Error playing file: $path", e)
        }
    }

    fun pause() {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyBoolean("pause", true)
            isPaused = true
        } catch (e: Throwable) {
            Log.e(TAG, "Error pausing MPV", e)
        }
    }

    fun unpause() {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyBoolean("pause", false)
            isPaused = false
        } catch (e: Throwable) {
            Log.e(TAG, "Error unpausing MPV", e)
        }
    }

    fun togglePause() {
        if (isPaused) unpause() else pause()
    }

    fun seekTo(positionSeconds: Double, mode: String = "absolute+exact") {
        if (!isInitialized) return
        try {
            mpvLib?.command(arrayOf("seek", positionSeconds.toString(), mode))
        } catch (e: Throwable) {
            Log.e(TAG, "Error seeking to $positionSeconds ($mode)", e)
        }
    }

    fun seekBy(offsetSeconds: Int) {
        if (!isInitialized) return
        try {
            mpvLib?.command(arrayOf("seek", offsetSeconds.toString(), "relative"))
        } catch (e: Throwable) {
            Log.e(TAG, "Error relative seek $offsetSeconds", e)
        }
    }

    fun setOptionString(name: String, value: String) {
        if (!isInitialized) return
        try {
            mpvLib?.setOptionString(name, value)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting option string $name=$value", e)
        }
    }

    fun applySubtitleOptions() {
        setOptionString("slang", "")
        setOptionString("sub-auto", "no")
        setOptionString("sub-codepage", "auto")
        setOptionString("embeddedfonts", "yes")
        setOptionString("sub-font-provider", "auto")
        setOptionString("sub-ass-override", "scale")
    }

    fun getPropertyString(name: String): String? {
        if (!isInitialized) return null
        return try {
            mpvLib?.getPropertyString(name)
        } catch (e: Throwable) {
            null
        }
    }

    fun setPropertyString(name: String, value: String) {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyString(name, value)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting property string $name=$value", e)
        }
    }

    fun setPropertyInt(name: String, value: Int) {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyInt(name, value)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting property int $name=$value", e)
        }
    }

    fun setPropertyBoolean(name: String, value: Boolean) {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyBoolean(name, value)
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting property boolean $name=$value", e)
        }
    }

    fun getPropertyInt(name: String): Int? {
        if (!isInitialized) return null
        return try {
            mpvLib?.getPropertyInt(name)
        } catch (e: Throwable) {
            null
        }
    }

    fun setPropertyFloat(name: String, value: Float) {
        if (!isInitialized) return
        try {
            mpvLib?.setPropertyDouble(name, value.toDouble())
        } catch (e: Throwable) {
            Log.e(TAG, "Error setting property float $name=$value", e)
        }
    }

    fun getPropertyDouble(name: String): Double? {
        if (!isInitialized) return null
        return try {
            mpvLib?.getPropertyDouble(name)
        } catch (e: Throwable) {
            null
        }
    }

    fun getChapterList(): List<ChapterNode> {
        if (!isInitialized) return emptyList()
        val lib = mpvLib ?: return emptyList()
        val list = mutableListOf<ChapterNode>()
        try {
            val count = lib.getPropertyInt("chapter-list/count") ?: 0
            for (i in 0 until count) {
                val title = lib.getPropertyString("chapter-list/$i/title") ?: ""
                val time = lib.getPropertyDouble("chapter-list/$i/time") ?: 0.0
                list.add(ChapterNode(title = title, time = time))
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting chapter list", e)
        }
        return list
    }

    fun getCurrentChapter(): Int? {
        if (!isInitialized) return null
        return try {
            mpvLib?.getPropertyInt("chapter")
        } catch (e: Throwable) {
            null
        }
    }

    fun getSubtitleText(): String? {
        return getPropertyString("sub-text")
    }

    fun getTrackList(): List<TrackNode> {
        if (!isInitialized) return emptyList()
        val lib = mpvLib ?: return emptyList()
        val list = mutableListOf<TrackNode>()
        try {
            val count = lib.getPropertyInt("track-list/count") ?: 0
            for (i in 0 until count) {
                val type = lib.getPropertyString("track-list/$i/type") ?: continue
                if (type != "sub" && type != "audio") continue
                val id = lib.getPropertyInt("track-list/$i/id") ?: (i + 1)
                val lang = lib.getPropertyString("track-list/$i/lang") ?: ""
                val title = lib.getPropertyString("track-list/$i/title") ?: ""
                val isDefault = lib.getPropertyBoolean("track-list/$i/default") ?: false
                val forced = lib.getPropertyBoolean("track-list/$i/forced") ?: false
                val external = lib.getPropertyBoolean("track-list/$i/external") ?: false
                val extFilename = lib.getPropertyString("track-list/$i/external-filename")

                list.add(
                    TrackNode(
                        id = id,
                        type = type,
                        lang = lang,
                        title = title,
                        isDefault = isDefault,
                        forced = forced,
                        external = external,
                        externalFilename = extFilename
                    )
                )
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error getting track list", e)
        }
        return list
    }

    fun detachSurface() {
        try {
            mpvLib?.detachSurface()
        } catch (e: Throwable) {
            Log.e(TAG, "Error detaching surface manually", e)
        }
    }

    fun command(args: Array<String>) {
        if (!isInitialized) return
        try {
            mpvLib?.command(args)
        } catch (e: Throwable) {
            Log.e(TAG, "Error running command: ${args.joinToString()}", e)
        }
    }

    fun updatePlaybackState() {
        if (!isInitialized) return
        try {
            val posSec = mpvLib?.getPropertyDouble("time-pos") ?: 0.0
            val durSec = mpvLib?.getPropertyDouble("duration") ?: 0.0
            val paused = mpvLib?.getPropertyBoolean("pause") ?: true
            val pausedForCache = mpvLib?.getPropertyBoolean("paused-for-cache") ?: false
            val cacheSec = mpvLib?.getPropertyDouble("demuxer-cache-time") ?: 0.0
            val eof = mpvLib?.getPropertyBoolean("eof-reached") ?: false
            val aspect = mpvLib?.getPropertyDouble("video-params/aspect") ?: 1.7777777777777777

            positionMs = (posSec * 1000).toLong().coerceAtLeast(0L)
            durationMs = (durSec * 1000).toLong().coerceAtLeast(0L)
            isPaused = paused
            isPausedForCache = pausedForCache
            cacheTimeSeconds = cacheSec
            isEofReached = eof
            if (aspect > 0) videoAspect = aspect
        } catch (e: Throwable) {
            // Property query exception ignored during initialization/no file
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
            Log.e(TAG, "Error stopping MPV", e)
        }
    }

    fun destroy() {
        if (!isInitialized) return
        try {
            mpvLib?.destroy()
            isInitialized = false
            mpvLib = null
            Log.d(TAG, "MPVLib destroyed")
        } catch (e: Throwable) {
            Log.e(TAG, "Error destroying MPVLib", e)
        }
    }

    fun onKey(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false
        return when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                togglePause()
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                seekBy(-10)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                seekBy(10)
                true
            }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                seekBy(10)
                true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                seekBy(-10)
                true
            }
            else -> false
        }
    }

    companion object {
        private const val TAG = "MPVView"
    }
}
