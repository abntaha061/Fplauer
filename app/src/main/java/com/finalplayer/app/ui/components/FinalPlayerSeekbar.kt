package com.finalplayer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.finalplayer.app.data.preferences.AppearancePreferences
import com.finalplayer.app.data.preferences.PlayerLayoutPreferences
import org.koin.compose.koinInject

@Composable
fun FinalPlayerSeekbar(
    position: Float,
    duration: Float,
    buffered: Float = 0f,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier,
    appearancePrefs: AppearancePreferences = koinInject(),
    layoutPrefs: PlayerLayoutPreferences = koinInject()
) {
    val layoutSeekbarStyle by layoutPrefs.seekbarStyle.asFlow().collectAsState(initial = "standard")
    val whiteProgressbar by layoutPrefs.whiteProgressbar.asFlow().collectAsState(initial = false)
    val appSeekbarStyle by appearancePrefs.seekbarStyle.asFlow().collectAsState(initial = "thin")
    val isGlass by appearancePrefs.glassmorphismSeekbar.asFlow().collectAsState(initial = false)

    val effectiveStyle = if (layoutSeekbarStyle != "standard") layoutSeekbarStyle else appSeekbarStyle

    val barHeight = when (effectiveStyle) {
        "thin", "simple" -> 3.dp
        "thick" -> 8.dp
        "wavy" -> 5.dp
        "circular" -> 4.dp
        else -> 4.dp
    }

    val progressColor = if (whiteProgressbar) Color.White else MaterialTheme.colorScheme.primary

    var isDragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(position) }
    val displayValue = if (isDragging) dragValue else position

    val trackBgColor = if (isGlass) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .padding(vertical = 6.dp)
    ) {
        // خلفية الشريط
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(barHeight / 2))
                .background(trackBgColor)
        )

        // مخزون (Buffered)
        if (buffered > 0f && duration > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((buffered / duration).coerceIn(0f, 1f))
                    .height(barHeight)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(barHeight / 2))
                    .background(Color.White.copy(alpha = 0.55f))
            )
        }

        // التقدم (Progress)
        if (duration > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((displayValue / duration).coerceIn(0f, 1f))
                    .height(barHeight)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(barHeight / 2))
                    .background(progressColor)
            )
        }

        // Thumb دائري
        if (duration > 0f) {
            val fraction = (displayValue / duration).coerceIn(0f, 1f)
            val thumbSize = when (effectiveStyle) {
                "thick" -> 16.dp
                "circular" -> 14.dp
                "simple" -> 8.dp
                else -> 12.dp
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction)
                    .wrapContentWidth(Alignment.End)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isDragging) thumbSize + 4.dp else thumbSize)
                        .background(
                            if (whiteProgressbar) Color.White else progressColor,
                            CircleShape
                        )
                )
            }
        }

        // منطقة اللمس الكاملة (سحب أو نكر مباشر)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(duration) {
                    detectTapGestures { offset ->
                        if (duration > 0f) {
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            val targetPos = fraction * duration
                            onValueChange(targetPos)
                            onValueChangeFinished(targetPos)
                        }
                    }
                }
                .pointerInput(duration) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragValue = position
                        },
                        onDragEnd = {
                            isDragging = false
                            onValueChangeFinished(dragValue)
                        },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (duration > 0f) {
                                val delta = dragAmount / size.width * duration
                                dragValue = (dragValue + delta).coerceIn(0f, duration)
                                onValueChange(dragValue)
                            }
                        }
                    )
                }
        )
    }
}

