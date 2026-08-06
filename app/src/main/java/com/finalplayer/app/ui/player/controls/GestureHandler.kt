package com.finalplayer.app.ui.player.controls

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

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
    content: @Composable () -> Unit = {}
) {
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
            // LAYER 2: Vertical Drag Gestures (Brightness - Left 33%, Volume - Right 33%)
            .pointerInput(Unit) {
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
                            // dragAmount < 0 is drag UP (increase), dragAmount > 0 is drag DOWN (decrease)
                            val delta = -dragAmount
                            if (isLeftRegion) {
                                onVerticalBrightnessDrag(delta)
                            } else {
                                onVerticalVolumeDrag(delta)
                            }
                        }
                    }
                )
            }
            // LAYER 3: Horizontal Drag Gestures (Seek - Entire Screen)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        if (!isVerticalActive) {
                            isHorizontalActive = true
                            onHorizontalDragStart()
                        }
                    },
                    onDragEnd = {
                        if (isHorizontalActive) {
                            onHorizontalDragEnd()
                        }
                        isHorizontalActive = false
                    },
                    onDragCancel = {
                        if (isHorizontalActive) {
                            onHorizontalDragEnd()
                        }
                        isHorizontalActive = false
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (isHorizontalActive && !isVerticalActive) {
                            change.consume()
                            onHorizontalDrag(dragAmount, size.width.toFloat())
                        }
                    }
                )
            }
    ) {
        content()
    }
}
