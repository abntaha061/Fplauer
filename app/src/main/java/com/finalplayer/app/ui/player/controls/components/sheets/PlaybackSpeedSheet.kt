package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaybackSpeedSheet(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val quickSpeeds = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
    var sliderValue by remember { mutableFloatStateOf(currentSpeed) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "سرعة التشغيل",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // القيمة الحالية كبيرة
        Text(
            text = "${String.format(Locale.US, "%.2f", sliderValue)}x",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Slider
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onSpeedChange(it)
            },
            valueRange = 0.25f..4.0f,
            steps = 14,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0.25x", style = MaterialTheme.typography.labelSmall)
            Text("4.0x",  style = MaterialTheme.typography.labelSmall)
        }

        Spacer(Modifier.height(8.dp))

        // أزرار سريعة
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickSpeeds.forEach { speed ->
                FilterChip(
                    selected = kotlin.math.abs(sliderValue - speed) < 0.01f,
                    onClick = {
                        sliderValue = speed
                        onSpeedChange(speed)
                    },
                    label = { Text("${speed}x") }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
