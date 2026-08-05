package com.finalplayer.app.player.core

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MPVController(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mpvView: MPVView? = null
    private var pollingJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    fun attachView(view: MPVView) {
        this.mpvView = view
        view.initialize(context, context.filesDir)
        startPolling()
    }

    fun detachView() {
        pollingJob?.cancel()
        this.mpvView = null
    }

    fun play(path: String) {
        if (mpvView == null) {
            val view = MPVView(context)
            attachView(view)
        }
        mpvView?.playFile(path)
        _playerState.update {
            it.copy(
                isPlaying = true,
                currentFilePath = path,
                isBuffering = false
            )
        }
        startPolling()
    }

    fun togglePlayPause() {
        val current = _playerState.value
        if (current.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        mpvView?.pause()
        _playerState.update { it.copy(isPlaying = false) }
    }

    fun resume() {
        mpvView?.resume()
        _playerState.update { it.copy(isPlaying = true) }
    }

    fun seekTo(positionMs: Long) {
        mpvView?.seekTo(positionMs)
        _playerState.update { it.copy(positionMs = positionMs) }
    }

    fun stop() {
        mpvView?.stop()
        _playerState.update {
            it.copy(
                isPlaying = false,
                positionMs = 0L,
                durationMs = 0L,
                currentFilePath = null,
                isBuffering = false
            )
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                mpvView?.let { view ->
                    view.updatePlaybackState()
                    _playerState.update {
                        it.copy(
                            positionMs = view.positionMs,
                            durationMs = view.durationMs,
                            isPlaying = !view.isPaused
                        )
                    }
                }
                delay(500)
            }
        }
    }

    fun release() {
        detachView()
        mpvView?.destroy()
    }
}
