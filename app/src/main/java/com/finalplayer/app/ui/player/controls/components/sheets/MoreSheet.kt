package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.player.Sheets
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSheet(
    sleepTimerRemaining: Int = 0,
    onOpenSheet: (Sheets) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "المزيد",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val items = listOf(
            Triple(Icons.Default.ClosedCaption, "الترجمة",   Sheets.SubtitleTracks),
            Triple(Icons.Default.Audiotrack,    "الصوت",     Sheets.AudioTracks),
            Triple(Icons.Default.BookmarkBorder,"الفصول",   Sheets.Chapters),
            Triple(Icons.Default.Speed,         "السرعة",   Sheets.PlaybackSpeed),
            Triple(Icons.Default.Memory,        "فك الترميز", Sheets.Decoders),
            Triple(Icons.Default.AspectRatio,   "نسبة العرض",Sheets.AspectRatios),
            Triple(Icons.Default.ZoomIn,        "تكبير",    Sheets.VideoZoom),
        )

        items.forEach { (icon, label, sheet) ->
            ListItem(
                headlineContent = { Text(label) },
                leadingContent  = { Icon(icon, null) },
                modifier = Modifier.clickable {
                    onDismiss()
                    onOpenSheet(sheet)
                }
            )
        }

        // مؤقت النوم مع عرض الوقت المتبقي
        ListItem(
            headlineContent = {
                Text(if (sleepTimerRemaining > 0)
                    String.format(Locale.US, "مؤقت النوم (%d:%02d)", sleepTimerRemaining / 60, sleepTimerRemaining % 60)
                else "مؤقت النوم")
            },
            leadingContent = { Icon(Icons.Default.Bedtime, null) },
            modifier = Modifier.clickable {
                onDismiss()
                onOpenSheet(Sheets.SleepTimer)
            }
        )

        Spacer(Modifier.height(32.dp))
    }
}
