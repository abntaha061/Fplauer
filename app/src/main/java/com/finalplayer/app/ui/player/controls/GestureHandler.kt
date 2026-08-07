package com.finalplayer.app.ui.player.controls

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.finalplayer.app.data.preferences.GesturePreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import org.koin.compose.koinInject
import kotlin.math.abs

@Composable
fun GestureHandler(
    onSingleTap: () -> Unit,
    onLeftDoubleTap: () -> Unit,
    onRightDoubleTap: () -> Unit,
    onCenterDoubleTap: () -> Unit,
    onVerticalBrightnessDrag: (Float) -> Unit,
    onVerticalVolumeDrag: (Float) -> Unit,
    onHorizontalDragStart: () -> Unit,
    onHorizontalDrag: (deltaPx: Float, screenWidthPx: Float) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
    gesturePrefs: GesturePreferences = koinInject(),
    playerPrefs: PlayerPreferences = koinInject(),
    content: @Composable () -> Unit = {}
) {
    val brightnessEnabled by gesturePrefs.brightnessGestureEnabled.asFlow().collectAsState(initial = true)
    val volumeEnabled by gesturePrefs.volumeGestureEnabled.asFlow().collectAsState(initial = true)
    val seekEnabled by gesturePrefs.seekGestureEnabled.asFlow().collectAsState(initial = true)
    val sensitivity by gesturePrefs.gestureSensitivity.asFlow().collectAsState(initial = 1.0f)
    val swipeSpeed by gesturePrefs.swipeSeekSpeed.asFlow().collectAsState(initial = 1.0f)
    val preventAccidental by gesturePrefs.preventAccidentalSeek.asFlow().collectAsState(initial = false)
    val swapVolBright by playerPrefs.swapVolumeAndBrightness.asFlow().collectAsState(initial = false)

    // Shared gesture direction lock flags to ensure horizontal cancels vertical and vice-versa
    var isHorizontalActive by remember { mutableStateOf(false) }
    var isVerticalActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            // LAYER 1: Tap Gestures (Single Tap, Double Tap Left/Right/Center)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onSingleTap()
                    },
                    onDoubleTap = { offset ->
                        val width = size.width
                        val x = offset.x
                        when {
                            x < width * 0.33f -> onLeftDoubleTap()
                            x > width * 0.67f -> onRightDoubleTap()
                            else -> onCenterDoubleTap()
                        }
                    }
                )
            }
            // LAYER 2: Vertical Drag Gestures (Brightness / Volume according to preferences)
            .pointerInput(brightnessEnabled, volumeEnabled, swapVolBright, sensitivity) {
                var isLeftRegion = false
                detectVerticalDragGestures(
                    onDragStart = { startOffset ->
                        if (!isHorizontalActive) {
                            val width = size.width
                            isLeftRegion = startOffset.x < width * 0.5f
                            isVerticalActive = true
                        }
                    },
                    onDragEnd = {
                        isVerticalActive = false
                    },
                    onDragCancel = {
                        isVerticalActive = false
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (isVerticalActive && !isHorizontalActive) {
                            change.consume()
                            val delta = -dragAmount * sensitivity
                            val isBrightnessTarget = if (swapVolBright) !isLeftRegion else isLeftRegion

                            if (isBrightnessTarget) {
                                if (brightnessEnabled) onVerticalBrightnessDrag(delta)
                            } else {
                                if (volumeEnabled) onVerticalVolumeDrag(delta)
                            }
                        }
                    }
                )
            }
            // LAYER 3: Horizontal Drag Gestures (Seek - Entire Screen)
            .pointerInput(seekEnabled, swipeSpeed, preventAccidental) {
                if (seekEnabled) {
                    var totalDragPx = 0f
                    var thresholdReached = false

                    detectHorizontalDragGestures(
                        onDragStart = {
                            if (!isVerticalActive) {
                                isHorizontalActive = true
                                totalDragPx = 0f
                                thresholdReached = !preventAccidental
                            }
                        },
                        onDragEnd = {
                            if (isHorizontalActive) {
                                if (thresholdReached) onHorizontalDragEnd()
                            }
                            isHorizontalActive = false
                            thresholdReached = false
                        },
                        onDragCancel = {
                            if (isHorizontalActive) {
                                if (thresholdReached) onHorizontalDragEnd()
                            }
                            isHorizontalActive = false
                            thresholdReached = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            if (isHorizontalActive && !isVerticalActive) {
                                change.consume()
                                totalDragPx += dragAmount
                                if (!thresholdReached) {
                                    if (abs(totalDragPx) > 30f) {
                                        thresholdReached = true
                                        onHorizontalDragStart()
                                    }
                                } else {
                                    val scaledAmount = dragAmount * swipeSpeed
                                    onHorizontalDrag(scaledAmount, size.width.toFloat())
                                }
                            }
                        }
                    )
                }
            }
    ) {
        content()
    }
}
