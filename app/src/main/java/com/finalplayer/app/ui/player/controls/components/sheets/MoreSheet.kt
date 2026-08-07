package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalplayer.app.data.preferences.PlayerLayoutPreferences
import com.finalplayer.app.ui.components.SidePanel
import com.finalplayer.app.ui.player.Sheets
import com.finalplayer.app.ui.settings.layout.ControlTools
import org.koin.compose.koinInject
import java.util.Locale

@Composable
fun MoreSheet(
    sleepTimerRemaining: Int = 0,
    onOpenSheet: (Sheets) -> Unit,
    onDismiss: () -> Unit,
    onToggleRotate: () -> Unit = {},
    onToggleLock: () -> Unit = {},
    onEnterPiP: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    onToggleShuffle: () -> Unit = {},
    onFrameStep: (Boolean) -> Unit = {},
    onFlipVideo: (Boolean) -> Unit = {},
    onToggleAbRepeat: () -> Unit = {},
    onCustomSkip: () -> Unit = {},
    onToggleCinema: () -> Unit = {},
    onToggleBackgroundPlay: () -> Unit = {},
    layoutPrefs: PlayerLayoutPreferences = koinInject()
) {
    SidePanel(onDismissRequest = onDismiss, scrollable = true) {
        Text(
            "أدوات التحكم والخيارات",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val toolIds = layoutPrefs.parseControlList(layoutPrefs.controlsTabButtons.get())

        toolIds.forEach { id ->
            val tool = ControlTools.getById(id)
            if (tool != null) {
                val sheetTarget = when (id) {
                    "subtitles" -> Sheets.SubtitleTracks
                    "audio_track" -> Sheets.AudioTracks
                    "chapters", "current_chapter" -> Sheets.Chapters
                    "speed" -> Sheets.PlaybackSpeed
                    "decoder" -> Sheets.Decoders
                    "aspect_ratio" -> Sheets.AspectRatios
                    "zoom" -> Sheets.VideoZoom
                    "sleep_timer" -> Sheets.SleepTimer
                    "frame_nav" -> Sheets.FrameNav
                    else -> null
                }

                val titleText = if (id == "sleep_timer" && sleepTimerRemaining > 0) {
                    String.format(Locale.US, "%s (%d:%02d)", tool.title, sleepTimerRemaining / 60, sleepTimerRemaining % 60)
                } else {
                    tool.title
                }

                ListItem(
                    headlineContent = { Text(titleText) },
                    leadingContent = { Icon(tool.icon, contentDescription = tool.title) },
                    modifier = Modifier.clickable {
                        onDismiss()
                        if (sheetTarget != null) {
                            onOpenSheet(sheetTarget)
                        } else {
                            when (id) {
                                "rotate" -> onToggleRotate()
                                "lock" -> onToggleLock()
                                "pip" -> onEnterPiP()
                                "repeat_mode" -> onToggleRepeat()
                                "shuffle" -> onToggleShuffle()
                                "flip_v" -> onFlipVideo(true)
                                "flip_h" -> onFlipVideo(false)
                                "ab_repeat" -> onToggleAbRepeat()
                                "custom_skip" -> onCustomSkip()
                                "cinema" -> onToggleCinema()
                                "background_play" -> onToggleBackgroundPlay()
                            }
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
