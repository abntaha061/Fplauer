package com.finalplayer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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

@Composable
fun FinalPlayerSeekbar(
    position: Float,
    duration: Float,
    buffered: Float = 0f,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragValue by remember { mutableFloatStateOf(position) }
    val displayValue = if (isDragging) dragValue else position

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)  // منطقة اللمس أكبر من الشريط نفسه
            .padding(vertical = 8.dp)  // الشريط المرئي = 4dp فعلياً
    ) {
        // خلفية الشريط (رمادي شفاف)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.3f))
        )

        // مخزون (Buffered) — لون أفتح
        if (buffered > 0f && duration > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((buffered / duration).coerceIn(0f, 1f))
                    .height(4.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }

        // التقدم (Progress) — أخضر
        if (duration > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((displayValue / duration).coerceIn(0f, 1f))
                    .height(4.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        // Thumb دائري صغير
        if (duration > 0f) {
            val fraction = (displayValue / duration).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction)
                    .wrapContentWidth(Alignment.End)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isDragging) 14.dp else 10.dp)  // يكبر عند السحب
                        .background(
                            Color.White,
                            CircleShape
                        )
                )
            }
        }

        // منطقة اللمس الكاملة
        Box(
            modifier = Modifier
                .fillMaxSize()
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
