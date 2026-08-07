package com.finalplayer.app.ui.player.controls

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.ZoomIn
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

import com.finalplayer.app.data.preferences.PlayerLayoutPreferences
import com.finalplayer.app.ui.settings.layout.ControlTools
import org.koin.compose.koinInject

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
    isLocked: Boolean = false,
    repeatMode: Int = 0,
    isShuffle: Boolean = false,
    isCinemaMode: Boolean = false,
    isBackgroundPlay: Boolean = false,
    currentAspectRatio: String = "default",
    currentVideoZoom: Float = 1.0f,
    onToggleLock: () -> Unit = {},
    onToggleRotate: () -> Unit = {},
    onEnterPiP: () -> Unit = {},
    onToggleRepeat: () -> Unit = {},
    onToggleShuffle: () -> Unit = {},
    onFrameStep: (Boolean) -> Unit = {},
    onFlipVideo: (Boolean) -> Unit = {},
    onToggleAbRepeat: () -> Unit = {},
    onCustomSkip: () -> Unit = {},
    onToggleCinema: () -> Unit = {},
    onToggleBackgroundPlay: () -> Unit = {},
    onSetAspectRatio: (String) -> Unit = {},
    onSetVideoZoom: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
    layoutPrefs: PlayerLayoutPreferences = koinInject()
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragPositionSeconds by remember { mutableFloatStateOf(0f) }
    var showRemainingTimeText by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }

    val isAnySheetOpen = sheetShown !is Sheets.None || showSleepTimerSheet

    val hideTimeoutMs by layoutPrefs.controlsHideTimeoutMs.asFlow().collectAsState(initial = 3000)
    val gradientOpacity by layoutPrefs.controlsGradientOpacity.asFlow().collectAsState(initial = 0.45f)

    val topRightRaw by layoutPrefs.topRightControls.asFlow().collectAsState(initial = PlayerLayoutPreferences.DEFAULT_TOP_RIGHT)
    val bottomRightRaw by layoutPrefs.bottomRightControls.asFlow().collectAsState(initial = PlayerLayoutPreferences.DEFAULT_BOTTOM_RIGHT)
    val bottomLeftRaw by layoutPrefs.bottomLeftControls.asFlow().collectAsState(initial = PlayerLayoutPreferences.DEFAULT_BOTTOM_LEFT)
    val portraitBottomRaw by layoutPrefs.portraitBottomControls.asFlow().collectAsState(initial = PlayerLayoutPreferences.DEFAULT_PORTRAIT_BOTTOM)

    val topRightControlIds = layoutPrefs.parseControlList(topRightRaw)
    val bottomRightControlIds = layoutPrefs.parseControlList(bottomRightRaw)
    val bottomLeftControlIds = layoutPrefs.parseControlList(bottomLeftRaw)
    val portraitBottomControlIds = layoutPrefs.parseControlList(portraitBottomRaw)

    // Auto-hide controls after configured timeout unless paused, dragging, or sheet open
    LaunchedEffect(controlsVisible, isPaused, isDraggingSlider, isAnySheetOpen, hideTimeoutMs) {
        if (controlsVisible && !isPaused && !isDraggingSlider && !isAnySheetOpen && hideTimeoutMs > 0) {
            delay(hideTimeoutMs.toLong())
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
                        .background(Color.Black.copy(alpha = gradientOpacity))
                )
            }

            if (isLocked) {
                IconButton(
                    onClick = onToggleLock,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "إلغاء القفل",
                        tint = Color.White
                    )
                }
            }

            // CONTROLS OVERLAY
            AnimatedVisibility(
                visible = controlsVisible && !isLocked,
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
                            topRightControlIds.forEach { id ->
                                RenderControlToolItem(
                                    id = id,
                                    currentDecoder = currentDecoder,
                                    playbackSpeed = playbackSpeed,
                                    chapters = chapters,
                                    currentChapterIndex = currentChapterIndex,
                                    selectedSubId = selectedSubId,
                                    selectedSecondarySubId = selectedSecondarySubId,
                                    remainingSleepTimerSeconds = remainingSleepTimerSeconds,
                                    repeatMode = repeatMode,
                                    isShuffle = isShuffle,
                                    onOpenSheet = onOpenSheet,
                                    onToggleRotate = onToggleRotate,
                                    onToggleLock = onToggleLock,
                                    onEnterPiP = onEnterPiP,
                                    onToggleRepeat = onToggleRepeat,
                                    onToggleShuffle = onToggleShuffle,
                                    onFrameStep = onFrameStep,
                                    onFlipVideo = onFlipVideo,
                                    onToggleAbRepeat = onToggleAbRepeat,
                                    onCustomSkip = onCustomSkip,
                                    onToggleCinema = onToggleCinema,
                                    onToggleBackgroundPlay = onToggleBackgroundPlay
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = formatTime(currentPos),
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                                )
                                bottomLeftControlIds.forEach { id ->
                                    RenderControlToolItem(
                                        id = id,
                                        currentDecoder = currentDecoder,
                                        playbackSpeed = playbackSpeed,
                                        chapters = chapters,
                                        currentChapterIndex = currentChapterIndex,
                                        selectedSubId = selectedSubId,
                                        selectedSecondarySubId = selectedSecondarySubId,
                                        remainingSleepTimerSeconds = remainingSleepTimerSeconds,
                                        repeatMode = repeatMode,
                                        isShuffle = isShuffle,
                                        onOpenSheet = onOpenSheet,
                                        onToggleRotate = onToggleRotate,
                                        onToggleLock = onToggleLock,
                                        onEnterPiP = onEnterPiP,
                                        onToggleRepeat = onToggleRepeat,
                                        onToggleShuffle = onToggleShuffle,
                                        onFrameStep = onFrameStep,
                                        onFlipVideo = onFlipVideo,
                                        onToggleAbRepeat = onToggleAbRepeat,
                                        onCustomSkip = onCustomSkip,
                                        onToggleCinema = onToggleCinema,
                                        onToggleBackgroundPlay = onToggleBackgroundPlay
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                bottomRightControlIds.forEach { id ->
                                    RenderControlToolItem(
                                        id = id,
                                        currentDecoder = currentDecoder,
                                        playbackSpeed = playbackSpeed,
                                        chapters = chapters,
                                        currentChapterIndex = currentChapterIndex,
                                        selectedSubId = selectedSubId,
                                        selectedSecondarySubId = selectedSecondarySubId,
                                        remainingSleepTimerSeconds = remainingSleepTimerSeconds,
                                        repeatMode = repeatMode,
                                        isShuffle = isShuffle,
                                        onOpenSheet = onOpenSheet,
                                        onToggleRotate = onToggleRotate,
                                        onToggleLock = onToggleLock,
                                        onEnterPiP = onEnterPiP,
                                        onToggleRepeat = onToggleRepeat,
                                        onToggleShuffle = onToggleShuffle,
                                        onFrameStep = onFrameStep,
                                        onFlipVideo = onFlipVideo,
                                        onToggleAbRepeat = onToggleAbRepeat,
                                        onCustomSkip = onCustomSkip,
                                        onToggleCinema = onToggleCinema,
                                        onToggleBackgroundPlay = onToggleBackgroundPlay
                                    )
                                }
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

                        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
                        if (isPortrait && portraitBottomControlIds.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                portraitBottomControlIds.forEach { id ->
                                    RenderControlToolItem(
                                        id = id,
                                        currentDecoder = currentDecoder,
                                        playbackSpeed = playbackSpeed,
                                        chapters = chapters,
                                        currentChapterIndex = currentChapterIndex,
                                        selectedSubId = selectedSubId,
                                        selectedSecondarySubId = selectedSecondarySubId,
                                        remainingSleepTimerSeconds = remainingSleepTimerSeconds,
                                        repeatMode = repeatMode,
                                        isShuffle = isShuffle,
                                        onOpenSheet = onOpenSheet,
                                        onToggleRotate = onToggleRotate,
                                        onToggleLock = onToggleLock,
                                        onEnterPiP = onEnterPiP,
                                        onToggleRepeat = onToggleRepeat,
                                        onToggleShuffle = onToggleShuffle,
                                        onFrameStep = onFrameStep,
                                        onFlipVideo = onFlipVideo,
                                        onToggleAbRepeat = onToggleAbRepeat,
                                        onCustomSkip = onCustomSkip,
                                        onToggleCinema = onToggleCinema,
                                        onToggleBackgroundPlay = onToggleBackgroundPlay
                                    )
                                }
                            }
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
                onDismiss = onCloseSheet,
                onToggleRotate = onToggleRotate,
                onToggleLock = onToggleLock,
                onEnterPiP = onEnterPiP,
                onToggleRepeat = onToggleRepeat,
                onToggleShuffle = onToggleShuffle,
                onFrameStep = onFrameStep,
                onFlipVideo = onFlipVideo,
                onToggleAbRepeat = onToggleAbRepeat,
                onCustomSkip = onCustomSkip,
                onToggleCinema = onToggleCinema,
                onToggleBackgroundPlay = onToggleBackgroundPlay
            )
        }
        is Sheets.AspectRatios -> {
            AspectRatiosSheet(
                currentRatio = currentAspectRatio,
                onSelectRatio = { ratio ->
                    onSetAspectRatio(ratio)
                    onCloseSheet()
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.VideoZoom -> {
            VideoZoomSheet(
                currentZoom = currentVideoZoom,
                onSelectZoom = { zoom ->
                    onSetVideoZoom(zoom)
                    onCloseSheet()
                },
                onDismiss = onCloseSheet
            )
        }
        is Sheets.FrameNav -> {
            FrameNavSheet(
                onStepFrame = { forward -> onFrameStep(forward) },
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

@Composable
private fun RenderControlToolItem(
    id: String,
    currentDecoder: Decoder,
    playbackSpeed: Float,
    chapters: List<ChapterNode>,
    currentChapterIndex: Int?,
    selectedSubId: Int?,
    selectedSecondarySubId: Int?,
    remainingSleepTimerSeconds: Int,
    repeatMode: Int = 0,
    isShuffle: Boolean = false,
    onOpenSheet: (Sheets) -> Unit,
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
    onToggleBackgroundPlay: () -> Unit = {}
) {
    when (id) {
        "decoder" -> {
            val decoderBadgeColor = when (currentDecoder) {
                Decoder.HW_PLUS -> Color(0xFF4CAF50)
                Decoder.HW_COPY -> Color(0xFF2196F3)
                Decoder.SOFTWARE -> Color(0xFFFF9800)
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
        }
        "speed" -> {
            Surface(
                color = if (kotlin.math.abs(playbackSpeed - 1.0f) > 0.01f) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f),
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
        "chapters", "current_chapter" -> {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenSheet(Sheets.Chapters) }
                    .testTag("chapter_top_badge")
            ) {
                val currentChapterText = if (chapters.isNotEmpty()) "فصل ${(currentChapterIndex ?: 0) + 1}/${chapters.size}" else "فصول"
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
        "subtitles" -> {
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
        }
        "audio_track" -> {
            IconButton(
                onClick = { onOpenSheet(Sheets.AudioTracks) },
                modifier = Modifier.testTag("audio_tracks_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Audiotrack,
                    contentDescription = "الصوت",
                    tint = Color.White
                )
            }
        }
        "aspect_ratio" -> {
            IconButton(
                onClick = { onOpenSheet(Sheets.AspectRatios) },
                modifier = Modifier.testTag("aspect_ratio_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = "نسبة العرض",
                    tint = Color.White
                )
            }
        }
        "zoom" -> {
            IconButton(
                onClick = { onOpenSheet(Sheets.VideoZoom) },
                modifier = Modifier.testTag("zoom_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "تكبير",
                    tint = Color.White
                )
            }
        }
        "sleep_timer" -> {
            IconButton(
                onClick = { onOpenSheet(Sheets.SleepTimer) },
                modifier = Modifier.testTag("sleep_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "مؤقت النوم",
                    tint = if (remainingSleepTimerSeconds > 0) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }
        "more" -> {
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
        "rotate" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleRotate) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "تدوير الشاشة", tint = Color.White)
            }
        }
        "lock" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleLock) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "قفل عناصر التحكم", tint = Color.White)
            }
        }
        "pip" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onEnterPiP) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "صورة داخل صورة", tint = Color.White)
            }
        }
        "repeat_mode" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleRepeat) {
                Icon(
                    imageVector = tool?.icon ?: Icons.Default.MoreVert,
                    contentDescription = "وضع التكرار",
                    tint = if (repeatMode > 0) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }
        "shuffle" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    imageVector = tool?.icon ?: Icons.Default.MoreVert,
                    contentDescription = "تشغيل عشوائي",
                    tint = if (isShuffle) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }
        "flip_v" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = { onFlipVideo(true) }) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "قلب رأسي", tint = Color.White)
            }
        }
        "flip_h" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = { onFlipVideo(false) }) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "قلب أفقي", tint = Color.White)
            }
        }
        "ab_repeat" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleAbRepeat) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "تكرار A-B", tint = Color.White)
            }
        }
        "frame_nav" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = { onOpenSheet(Sheets.FrameNav) }) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "التنقل بين الإطارات", tint = Color.White)
            }
        }
        "custom_skip" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onCustomSkip) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "تخطي مخصص", tint = Color.White)
            }
        }
        "cinema" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleCinema) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "الوضع السينمائي", tint = Color.White)
            }
        }
        "background_play" -> {
            val tool = ControlTools.getById(id)
            IconButton(onClick = onToggleBackgroundPlay) {
                Icon(imageVector = tool?.icon ?: Icons.Default.MoreVert, contentDescription = "التشغيل في الخلفية", tint = Color.White)
            }
        }
        else -> {
            val tool = ControlTools.getById(id)
            if (tool != null) {
                IconButton(onClick = { onOpenSheet(Sheets.More) }) {
                    Icon(imageVector = tool.icon, contentDescription = tool.title, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun AspectRatiosSheet(
    currentRatio: String,
    onSelectRatio: (String) -> Unit,
    onDismiss: () -> Unit
) {
    SidePanel(onDismissRequest = onDismiss, scrollable = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("نسبة العرض إلى الارتفاع", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            val options = listOf(
                "default" to "تلقائي (Fit)",
                "16:9" to "16:9",
                "4:3" to "4:3",
                "21:9" to "21:9",
                "fill" to "تعبئة الشاشة (Fill)"
            )
            options.forEach { (key, label) ->
                Surface(
                    color = if (currentRatio == key) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRatio(key) }
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoZoomSheet(
    currentZoom: Float,
    onSelectZoom: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    SidePanel(onDismissRequest = onDismiss, scrollable = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("تكبير الفيديو", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            val options = listOf(1.0f to "100% (عادي)", 1.25f to "125%", 1.5f to "150%", 1.75f to "175%", 2.0f to "200%")
            options.forEach { (zoom, label) ->
                Surface(
                    color = if (kotlin.math.abs(currentZoom - zoom) < 0.05f) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectZoom(zoom) }
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
fun FrameNavSheet(
    onStepFrame: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    SidePanel(onDismissRequest = onDismiss, scrollable = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("التنقل بين الإطارات", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { onStepFrame(false) }) {
                    Text("إطار للخلف")
                }
                Button(onClick = { onStepFrame(true) }) {
                    Text("إطار للأمام")
                }
            }
        }
    }
}
