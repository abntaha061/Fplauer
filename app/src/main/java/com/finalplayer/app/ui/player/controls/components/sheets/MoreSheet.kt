package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.player.Sheets

import android.content.Intent
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.platform.LocalContext
import com.finalplayer.app.ui.settings.SettingsActivity

private data class MoreOption(
    val title: String,
    val icon: ImageVector,
    val targetSheet: Sheets,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSheet(
    onOpenSheet: (Sheets) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val options = listOf(
        MoreOption("الترجمة", Icons.Outlined.Subtitles, Sheets.SubtitleTracks, "more_subtitles_option"),
        MoreOption("الصوت", Icons.Outlined.Audiotrack, Sheets.AudioTracks, "more_audio_option"),
        MoreOption("الفصول", Icons.Outlined.Bookmarks, Sheets.Chapters, "more_chapters_option"),
        MoreOption("جودة فك الترميز", Icons.Outlined.Memory, Sheets.Decoders, "more_decoder_option"),
        MoreOption("سرعة التشغيل", Icons.Outlined.Speed, Sheets.PlaybackSpeed, "more_speed_option"),
        MoreOption("مؤقت النوم", Icons.Outlined.Timer, Sheets.SleepTimer, "more_sleep_timer_option"),
        MoreOption("الإعدادات", Icons.Outlined.Settings, Sheets.None, "more_settings_option"),
        MoreOption("نسبة العرض", Icons.Outlined.AspectRatio, Sheets.None, "more_aspect_ratio_option")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("more_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "المزيد من الخيارات",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(options) { item ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onDismiss()
                                if (item.testTag == "more_settings_option") {
                                    val intent = Intent(context, SettingsActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } else if (item.targetSheet != Sheets.None) {
                                    onOpenSheet(item.targetSheet)
                                }
                            }
                            .testTag(item.testTag)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
