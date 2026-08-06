package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackSpeedSheet(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val quickSpeeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("playback_speed_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "سرعة التشغيل",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Large Centered Speed Display
            Text(
                text = String.format(Locale.US, "%.2fx", currentSpeed),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("current_speed_text")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Speed Slider: 0.25x to 4.0x
            Slider(
                value = currentSpeed,
                onValueChange = { rawVal ->
                    val roundedVal = (rawVal * 20).roundToInt() / 20f
                    onSpeedChange(roundedVal.coerceIn(0.25f, 4.0f))
                },
                valueRange = 0.25f..4.0f,
                steps = 74, // increments of 0.05
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playback_speed_slider")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Speed Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                quickSpeeds.forEach { speed ->
                    val isSelected = (currentSpeed - speed).let { kotlin.math.abs(it) < 0.04f }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSpeedChange(speed) },
                        label = {
                            Text(
                                text = "${speed}x",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("speed_chip_${speed}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
