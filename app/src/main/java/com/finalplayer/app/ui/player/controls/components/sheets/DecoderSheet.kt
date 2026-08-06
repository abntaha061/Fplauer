package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.player.Decoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecoderSheet(
    currentDecoder: Decoder,
    onSelectDecoder: (Decoder) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("decoder_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "جودة فك الترميز (Decoder)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DecoderOptionItem(
                title = "HW+ (Hardware Direct)",
                description = "الأسرع، أقل استهلاكاً للبطارية",
                badgeText = "موصى به",
                badgeColor = Color(0xFF4CAF50), // Green
                isSelected = currentDecoder == Decoder.HW_PLUS,
                onClick = { onSelectDecoder(Decoder.HW_PLUS) },
                testTag = "decoder_hw_plus_option"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DecoderOptionItem(
                title = "HW (Hardware Copy)",
                description = "متوافق مع معالجة إضافية (فلاتر/شيدرز)",
                badgeText = "متوافق",
                badgeColor = Color(0xFF2196F3), // Blue
                isSelected = currentDecoder == Decoder.HW_COPY,
                onClick = { onSelectDecoder(Decoder.HW_COPY) },
                testTag = "decoder_hw_copy_option"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DecoderOptionItem(
                title = "SW (Software)",
                description = "أبطأ لكن الأكثر توافقاً للملفات المشكلة",
                badgeText = "بطيء",
                badgeColor = Color(0xFFFF9800), // Orange
                isSelected = currentDecoder == Decoder.SOFTWARE,
                onClick = { onSelectDecoder(Decoder.SOFTWARE) },
                testTag = "decoder_sw_option"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DecoderOptionItem(
    title: String,
    description: String,
    badgeText: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
