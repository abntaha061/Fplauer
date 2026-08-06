package com.finalplayer.app.ui.player

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalplayer.app.player.PlayerViewModel
import com.finalplayer.app.player.core.MPVView
import com.finalplayer.app.ui.player.components.ResumeSnackbar
import com.finalplayer.app.ui.player.controls.PlayerControls
import com.finalplayer.app.ui.player.subtitle.SubtitleView

@Composable
fun PlayerScreen(
    videoPath: String,
    videoTitle: String,
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val isPaused by viewModel.paused.collectAsStateWithLifecycle()
    val positionSeconds by viewModel.precisePosition.collectAsStateWithLifecycle()
    val durationSeconds by viewModel.preciseDuration.collectAsStateWithLifecycle()
    val controlsVisible by viewModel.controlsShown.collectAsStateWithLifecycle()
    val sleepTimerSeconds by viewModel.remainingTime.collectAsStateWithLifecycle()

    val currentBrightness by viewModel.currentBrightness.collectAsStateWithLifecycle()
    val isBrightnessSliderShown by viewModel.isBrightnessSliderShown.collectAsStateWithLifecycle()
    val currentVolumePercent by viewModel.currentVolumePercent.collectAsStateWithLifecycle()
    val isVolumeSliderShown by viewModel.isVolumeSliderShown.collectAsStateWithLifecycle()
    val dragSeekState by viewModel.dragSeekState.collectAsStateWithLifecycle()
    val doubleTapSeekState by viewModel.doubleTapSeekState.collectAsStateWithLifecycle()

    val subtitleTracks by viewModel.subtitleTracks.collectAsStateWithLifecycle()
    val audioTracks by viewModel.audioTracks.collectAsStateWithLifecycle()
    val selectedSubId by viewModel.selectedSubId.collectAsStateWithLifecycle()
    val selectedSecondarySubId by viewModel.selectedSecondarySubId.collectAsStateWithLifecycle()
    val selectedAudioId by viewModel.selectedAudioId.collectAsStateWithLifecycle()
    val currentSubText by viewModel.currentSubText.collectAsStateWithLifecycle()

    val currentDecoder by viewModel.currentDecoder.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    val currentChapterIndex by viewModel.currentChapterIndex.collectAsStateWithLifecycle()
    val sheetShown by viewModel.sheetShown.collectAsStateWithLifecycle()

    val playlistItems by viewModel.playlistItems.collectAsStateWithLifecycle()
    val currentPlaylistIndex by viewModel.currentPlaylistIndex.collectAsStateWithLifecycle()
    val isPlaylistMode by viewModel.isPlaylistMode.collectAsStateWithLifecycle()
    val resumePositionSec by viewModel.resumePositionSec.collectAsStateWithLifecycle()

    val mpvView = remember { MPVView(context) }

    // Initialize initial system audio & brightness
    LaunchedEffect(Unit) {
        viewModel.initBrightness(activity?.window, context)
        viewModel.initVolume(context)
    }

    // Keep screen on while player is active
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Attach view & initialize title
    DisposableEffect(mpvView) {
        viewModel.onFileLoaded(videoTitle)
        viewModel.mpvController.attachView(mpvView)

        mpvView.onSurfaceReady = {
            if (videoPath.isNotEmpty()) {
                viewModel.mpvController.play(videoPath)
            }
        }

        onDispose {
            viewModel.mpvController.detachView()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { mpvView },
            modifier = Modifier.fillMaxSize()
        )

        SubtitleView(
            subtitleText = currentSubText,
            modifier = Modifier.fillMaxSize()
        )

        PlayerControls(
            title = videoTitle,
            isPaused = isPaused ?: true,
            positionSeconds = positionSeconds,
            durationSeconds = durationSeconds,
            controlsVisible = controlsVisible,
            remainingSleepTimerSeconds = sleepTimerSeconds,
            brightnessValue = currentBrightness,
            isBrightnessSliderShown = isBrightnessSliderShown,
            volumePercent = currentVolumePercent,
            isVolumeSliderShown = isVolumeSliderShown,
            dragSeekState = dragSeekState,
            doubleTapSeekState = doubleTapSeekState,
            onToggleControls = { viewModel.toggleControls() },
            onPlayPause = { viewModel.pauseUnpause() },
            onSeekTo = { pos -> viewModel.seekTo(pos) },
            onSeekBy = { offset -> viewModel.seekBy(offset) },
            onLeftDoubleTap = { viewModel.leftSeek() },
            onRightDoubleTap = { viewModel.rightSeek() },
            onCenterDoubleTap = { viewModel.pauseUnpause() },
            onVerticalBrightnessDrag = { delta ->
                viewModel.changeBrightnessBy(delta, activity?.window, context)
            },
            onVerticalVolumeDrag = { delta ->
                viewModel.changeVolumeBy(delta, context)
            },
            onHorizontalDragStart = { viewModel.onHorizontalDragStart() },
            onHorizontalDrag = { delta, screenWidth ->
                viewModel.onHorizontalDrag(delta, screenWidth)
            },
            onHorizontalDragEnd = { viewModel.onHorizontalDragEnd() },
            onBackClick = onBackClick,
            onStartSleepTimer = { seconds -> viewModel.startTimer(seconds) },
            onCancelSleepTimer = { viewModel.cancelTimer() },
            subtitleTracks = subtitleTracks,
            audioTracks = audioTracks,
            selectedSubId = selectedSubId,
            selectedSecondarySubId = selectedSecondarySubId,
            selectedAudioId = selectedAudioId,
            currentDecoder = currentDecoder,
            playbackSpeed = playbackSpeed,
            chapters = chapters,
            currentChapterIndex = currentChapterIndex,
            sheetShown = sheetShown,
            onOpenSheet = { sheet -> viewModel.openSheet(sheet) },
            onCloseSheet = { viewModel.closeSheet() },
            onSelectSubtitle = { trackId -> viewModel.toggleSubtitle(trackId) },
            onDisableSubtitles = { viewModel.disableSubtitles() },
            onAddExternalSubtitle = { uri -> viewModel.addSubtitle(uri, context) },
            onRemoveSubtitle = { id -> viewModel.removeSubtitle(id) },
            onSelectAudioTrack = { id -> viewModel.selectAudioTrack(id) },
            onAddAudio = { uri -> viewModel.addAudio(uri, context) },
            onSelectDecoder = { dec -> viewModel.setDecoder(dec) },
            onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed) },
            onSelectChapter = { index -> viewModel.selectChapter(index) },
            isPlaylistMode = isPlaylistMode,
            currentPlaylistIndex = currentPlaylistIndex,
            totalPlaylistCount = playlistItems.size,
            playlistItems = playlistItems,
            onNextClick = { viewModel.playNextVideo() },
            onPreviousClick = { viewModel.playPreviousVideo() },
            onReorderPlaylist = { from, to -> viewModel.reorderPlaylist(from, to) },
            onSelectPlaylistItem = { idx -> viewModel.playPlaylistItem(idx) }
        )

        val resumePos = resumePositionSec
        if (resumePos != null && resumePos > 0) {
            ResumeSnackbar(
                savedPositionSec = resumePos,
                onResume = {
                    viewModel.seekTo(resumePos.toFloat())
                    viewModel.clearResumePosition()
                },
                onStartFromBeginning = {
                    viewModel.seekTo(0f)
                    viewModel.clearResumePosition()
                }
            )
        }
    }
}
