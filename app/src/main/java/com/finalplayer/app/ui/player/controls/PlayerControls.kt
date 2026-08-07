package com.finalplayer.app.ui.player.controls

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import com.finalplayer.app.ui.player.ChapterNode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.ui.player.controls.components.sheets.PlaylistSheet
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.finalplayer.app.player.DoubleTapSeekState
import com.finalplayer.app.player.SeekState
import com.finalplayer.app.ui.components.FinalPlayerSeekbar
import com.finalplayer.app.ui.components.SidePanel
import com.finalplayer.app.ui.player.controls.components.BrightnessSlider
import com.finalplayer.app.ui.player.controls.components.DoubleTapSeekOvals
import com.finalplayer.app.ui.player.controls.components.SeekOverlay
import com.finalplayer.app.ui.player.controls.components.VolumeSlider
import androidx.compose.material.icons.filled.MoreVert
import com.finalplayer.app.ui.player.Decoder
import com.finalplayer.app.ui.player.Sheets
import com.finalplayer.app.ui.player.controls.components.sheets.AudioTracksSheet
import com.finalplayer.app.ui.player.controls.components.sheets.ChaptersSheet
import com.finalplayer.app.ui.player.controls.components.sheets.DecoderSheet
import com.finalplayer.app.ui.player.controls.components.sheets.MoreSheet
import com.finalplayer.app.ui.player.controls.components.sheets.PlaybackSpeedSheet
import com.finalplayer.app.ui.player.controls.components.sheets.SubtitleSettingsPanel
import com.finalplayer.app.ui.player.controls.components.sheets.SubtitlesSheet
import com.finalplayer.app.ui.player.controls.components.sheets.TrackNode
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    title: String,
    isPaused: Boolean,
    positionSeconds: Float,
    durationSeconds: Float,
    controlsVisible: Boolean,
    remainingSleepTimerSeconds: Int,
    brightnessValue: Float,
    isBrightnessSliderShown: Boolean,
    volumePercent: Float,
    isVolumeSliderShown: Boolean,
    dragSeekState: SeekState?,
    doubleTapSeekState: DoubleTapSeekState?,
    onToggleControls: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekTo: (Float) -> Unit,
    onSeekBy: (Int) -> Unit,
    onLeftDoubleTap: () -> Unit,
    onRightDoubleTap: () -> Unit,
    onCenterDoubleTap: () -> Unit,
    onVerticalBrightnessDrag: (Float) -> Unit,
    onVerticalVolumeDrag: (Float) -> Unit,
    onHorizontalDragStart: () -> Unit,
    onHorizontalDrag: (deltaPx: Float, screenWidthPx: Float) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onBackClick: () -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    subtitleTracks: List<TrackNode> = emptyList(),
    audioTracks: List<TrackNode> = emptyList(),
    selectedSubId: Int? = 0,
    selectedSecondarySubId: Int? = 0,
    selectedAudioId: Int? = 0,
    currentDecoder: Decoder = Decoder.HW_PLUS,
    playbackSpeed: Float = 1.0f,
    chapters: List<ChapterNode> = emptyList(),
    currentChapterIndex: Int? = null,
    sheetShown: Sheets = Sheets.None,
    onOpenSheet: (Sheets) -> Unit = {},
    onCloseSheet: () -> Unit = {},
    onSelectSubtitle: (Int) -> Unit = {},
    onDisableSubtitles: () -> Unit = {},
    onAddExternalSubtitle: (Uri) -> Unit = {},
    onRemoveSubtitle: (Int) -> Unit = {},
    onSelectAudioTrack: (Int) -> Unit = {},
    onAddAudio: (Uri) -> Unit = {},
    onSelectDecoder: (Decoder) -> Unit = {},
    onSpeedChange: (Float) -> Unit = {},
    onSelectChapter: (Int) -> Unit = {},
    isPlaylistMode: Boolean = false,
    currentPlaylistIndex: Int = 0,
    totalPlaylistCount: Int = 0,
    playlistItems: List<VideoItem> = emptyList(),
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onReorderPlaylist: (Int, Int) -> Unit = { _, _ -> },
    onSelectPlaylistItem: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragPositionSeconds by remember { mutableFloatStateOf(0f) }
    var showRemainingTimeText by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }

    val isAnySheetOpen = sheetShown !is Sheets.None || showSleepTimerSheet

    // Auto-hide controls after 3 seconds of inactivity unless paused, dragging, or sheet open
    LaunchedEffect(controlsVisible, isPaused, isDraggingSlider, isAnySheetOpen) {
        if (controlsVisible && !isPaused && !isDraggingSlider && !isAnySheetOpen) {
            delay(3000)
            onToggleControls()
        }
    }

    GestureHandler(
        onSingleTap = onToggleControls,
        onLeftDoubleTap = onLeftDoubleTap,
        onRightDoubleTap = onRightDoubleTap,
        onCenterDoubleTap = onCenterDoubleTap,
        onVerticalBrightnessDrag = onVerticalBrightnessDrag,
        onVerticalVolumeDrag = onVerticalVolumeDrag,
        onHorizontalDragStart = onHorizontalDragStart,
        onHorizontalDrag = onHorizontalDrag,
        onHorizontalDragEnd = onHorizontalDragEnd,
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dark Overlay when controls are visible
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }

            // CONTROLS OVERLAY
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier.fillMaxSize()
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val (topBar, centerControls, bottomBar) = createRefs()

                    // TOP BAR
                    Row(
                        modifier = Modifier
                            .constrainAs(topBar) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color.White
                            )
                        }

                        val displayTitle = if (isPlaylistMode && totalPlaylistCount > 0) {
                            "$title • ${currentPlaylistIndex + 1}/$totalPlaylistCount"
                        } else {
                            title
                        }

                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                                .then(
                                    if (isPlaylistMode) {
                                        Modifier.clickable { onOpenSheet(Sheets.Playlist) }
                                    } else Modifier
                                )
                                .testTag("player_title_text")
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Decoder Badge
                            val decoderBadgeColor = when (currentDecoder) {
                                Decoder.HW_PLUS -> Color(0xFF4CAF50) // Green
                                Decoder.HW_COPY -> Color(0xFF2196F3) // Blue
                                Decoder.SOFTWARE -> Color(0xFFFF9800) // Orange
                            }
                            Surface(
                                color = decoderBadgeColor,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onOpenSheet(Sheets.Decoders) }
                                    .testTag("decoder_top_badge")
                            ) {
                                Text(
                                    text = currentDecoder.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Playback Speed Badge (if speed != 1.0x)
                            if (kotlin.math.abs(playbackSpeed - 1.0f) > 0.01f) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onOpenSheet(Sheets.PlaybackSpeed) }
                                        .testTag("speed_top_badge")
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.2fx", playbackSpeed),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Chapter Badge (if chapters exist)
                            if (chapters.isNotEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onOpenSheet(Sheets.Chapters) }
                                        .testTag("chapter_top_badge")
                                ) {
                                    val currentChapterText = "فصل ${(currentChapterIndex ?: 0) + 1}/${chapters.size}"
                                    Text(
                                        text = currentChapterText,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Subtitles Button
                            IconButton(
                                onClick = { onOpenSheet(Sheets.SubtitleTracks) },
                                modifier = Modifier.testTag("subtitles_button")
                            ) {
                                val hasSubSelected = (selectedSubId != null && selectedSubId > 0) ||
                                        (selectedSecondarySubId != null && selectedSecondarySubId > 0)
                                Icon(
                                    imageVector = Icons.Outlined.Subtitles,
                                    contentDescription = "الترجمة",
                                    tint = if (hasSubSelected) MaterialTheme.colorScheme.primary else Color.White
                                )
                            }

                            // Sleep Timer Button
                            IconButton(
                                onClick = { onOpenSheet(Sheets.SleepTimer) },
                                modifier = Modifier.testTag("sleep_timer_button")
                            ) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = "مؤقت النوم",
                                        tint = if (remainingSleepTimerSeconds > 0) MaterialTheme.colorScheme.primary else Color.White
                                    )
                                }
                            }

                            // More Button (3 Dots)
                            IconButton(
                                onClick = { onOpenSheet(Sheets.More) },
                                modifier = Modifier.testTag("more_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "المزيد",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // CENTER CONTROLS
                    Row(
                        modifier = Modifier.constrainAs(centerControls) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isPlaylistMode) {
                            IconButton(
                                onClick = onPreviousClick,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("player_previous_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "السابق",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onSeekBy(-10) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_rewind_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "تأخير 10 ثوانٍ",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Surface(
                            onClick = onPlayPause,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.55f),
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("player_play_pause_button")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = if (isPaused) "تشغيل" else "إيقاف مؤقت",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onSeekBy(10) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_ffwd_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "تقديم 10 ثوانٍ",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        if (isPlaylistMode) {
                            IconButton(
                                onClick = onNextClick,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("player_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "التالي",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // BOTTOM BAR
                    Column(
                        modifier = Modifier.constrainAs(bottomBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        val currentPos = if (isDraggingSlider) dragPositionSeconds else positionSeconds
                        val safeDuration = if (durationSeconds > 0f) durationSeconds else 1f

                        FinalPlayerSeekbar(
                            position = currentPos,
                            duration = safeDuration,
                            onValueChange = { newValue ->
                                isDraggingSlider = true
                                dragPositionSeconds = newValue
                            },
                            onValueChangeFinished = {
                                onSeekTo(dragPositionSeconds)
                                isDraggingSlider = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_seek_slider")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentPos),
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                            )

                            Text(
                                text = if (showRemainingTimeText) {
                                    "-${formatTime((safeDuration - currentPos).coerceAtLeast(0f))}"
                                } else {
                                    formatTime(safeDuration)
                                },
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                                modifier = Modifier.clickable {
                                    showRemainingTimeText = !showRemainingTimeText
                                }
                            )
                        }
                    }
                }
            }

            // GESTURE OVERLAYS (BRIGHTNESS, VOLUME, DRAG SEEK, DOUBLE TAP)
            BrightnessSlider(
                brightnessValue = brightnessValue,
                isVisible = isBrightnessSliderShown,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            VolumeSlider(
                volumePercent = volumePercent,
                isVisible = isVolumeSliderShown,
                modifier = Modifier.align(Alignment.CenterEnd)
            )

            SeekOverlay(
                seekState = dragSeekState,
                modifier = Modifier.align(Alignment.Center)
            )

            DoubleTapSeekOvals(
                doubleTapState = doubleTapSeekState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            onAddAudio(uri)
        }
    }

    when (sheetShown) {
        is Sheets.SubtitleTracks -> {
            SubtitlesSheet(
                tracks = subtitleTracks,
                selectedSubId = selectedSubId,
                selectedSecondarySubId = selectedSecondarySubId,
                onSelectSubtitle = { trackId ->
                    onSelectSubtitle(trackId)
                    onCloseSheet()
                },
                onDisableSubtitles = {
                    onDisableSubtitles()
                    onCloseSheet()
                },
                onAddExternalSubtitle = { uri ->
                    onAddExternalSubtitle(uri)
                    onCloseSheet()
                },
                onRemoveSubtitle = onRemoveSubtitle,
                onOpenSettings = { onOpenSheet(Sheets.SubtitleSettings) },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.SubtitleSettings -> {
            SubtitleSettingsPanel(
                onDismiss = onCloseSheet
            )
        }
        is Sheets.AudioTracks -> {
            AudioTracksSheet(
                tracks = audioTracks,
                currentAudioId = selectedAudioId ?: 0,
                onSelectAudio = { id ->
                    onSelectAudioTrack(id)
                    onCloseSheet()
                },
                onAddAudioFile = {
                    audioPicker.launch(arrayOf("audio/*", "video/*", "*/*"))
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.Decoders -> {
            DecoderSheet(
                currentDecoder = currentDecoder,
                onSelect = { dec ->
                    onSelectDecoder(dec)
                    onCloseSheet()
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.PlaybackSpeed -> {
            PlaybackSpeedSheet(
                currentSpeed = playbackSpeed,
                onSpeedChange = { speed ->
                    onSpeedChange(speed)
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.Chapters -> {
            ChaptersSheet(
                chapters = chapters,
                currentChapterIndex = currentChapterIndex,
                onSeekToChapter = { idx ->
                    onSelectChapter(idx)
                    onCloseSheet()
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.More -> {
            MoreSheet(
                sleepTimerRemaining = remainingSleepTimerSeconds,
                onOpenSheet = onOpenSheet,
                onDismiss = onCloseSheet
            )
        }
        is Sheets.Playlist -> {
            PlaylistSheet(
                playlistItems = playlistItems,
                currentVideoIndex = currentPlaylistIndex,
                onDismiss = onCloseSheet,
                onVideoSelect = { idx ->
                    onSelectPlaylistItem(idx)
                    onCloseSheet()
                },
                onReorder = onReorderPlaylist
            )
        }
        is Sheets.SleepTimer -> {
            SleepTimerBottomSheet(
                currentRemainingSeconds = remainingSleepTimerSeconds,
                onDismiss = onCloseSheet,
                onStartTimer = { seconds ->
                    onStartSleepTimer(seconds)
                    onCloseSheet()
                },
                onCancelTimer = {
                    onCancelSleepTimer()
                    onCloseSheet()
                }
            )
        }
        else -> {
            if (showSleepTimerSheet) {
                SleepTimerBottomSheet(
                    currentRemainingSeconds = remainingSleepTimerSeconds,
                    onDismiss = { showSleepTimerSheet = false },
                    onStartTimer = { minutes ->
                        onStartSleepTimer(minutes * 60)
                        showSleepTimerSheet = false
                    },
                    onCancelTimer = {
                        onCancelSleepTimer()
                        showSleepTimerSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerBottomSheet(
    currentRemainingSeconds: Int,
    onDismiss: () -> Unit,
    onStartTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(if (currentRemainingSeconds > 0) currentRemainingSeconds / 60 else 30) }

    SidePanel(onDismissRequest = onDismiss, scrollable = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "مؤقت النوم",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$selectedMinutes دقيقة",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { if (selectedMinutes > 5) selectedMinutes -= 5 },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "تقليل")
                }

                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { selectedMinutes = it.toInt() },
                    valueRange = 5f..180f,
                    steps = 34,
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    track = { sliderState ->
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            modifier = Modifier.height(3.dp),
                            thumbTrackGapSize = 0.dp,
                            trackInsideCornerSize = 2.dp
                        )
                    },
                    thumb = {
                        Box(
                            Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                    }
                )

                IconButton(
                    onClick = { if (selectedMinutes < 180) selectedMinutes += 5 },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "زيادة")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick preset chips
            val presets = listOf(15, 30, 45, 60, 90, 120)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                presets.forEach { min ->
                    FilterChip(
                        selected = selectedMinutes == min,
                        onClick = { selectedMinutes = min },
                        label = { Text("${min}m") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelTimer,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("إلغاء المؤقت")
                }

                Button(
                    onClick = { onStartTimer(selectedMinutes) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("بدء المؤقت")
                }
            }
        }
    }
}

private fun formatTime(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val hrs = totalSec / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60

    return if (hrs > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }
}
