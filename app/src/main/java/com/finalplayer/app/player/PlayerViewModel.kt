package com.finalplayer.app.player

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.data.preferences.AppearancePreferences
import com.finalplayer.app.data.preferences.AudioPreferences
import com.finalplayer.app.data.preferences.DecoderPreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import com.finalplayer.app.player.core.MPVController
import com.finalplayer.app.player.core.TrackSelector
import com.finalplayer.app.ui.player.ChapterNode
import com.finalplayer.app.ui.player.Decoder
import com.finalplayer.app.ui.player.Sheets
import com.finalplayer.app.ui.player.controls.components.sheets.TrackNode
import com.finalplayer.app.domain.model.PlaybackProgress
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.domain.repository.PlaybackRepository
import com.finalplayer.app.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs

data class SeekState(
    val targetPositionSec: Float = 0f,
    val diffSeconds: Float = 0f,
    val isForwards: Boolean = true,
    val isDragging: Boolean = false
)

data class DoubleTapSeekState(
    val isLeft: Boolean,
    val amountSeconds: Int = 10,
    val timestamp: Long = System.currentTimeMillis()
)

class PlayerViewModel(
    val mpvController: MPVController,
    val playerPrefs: PlayerPreferences? = null,
    val subtitlesPrefs: SubtitlesPreferences? = null,
    val audioPrefs: AudioPreferences? = null,
    val decoderPrefs: DecoderPreferences? = null,
    val appearancePrefs: AppearancePreferences? = null
) : ViewModel(), KoinComponent {

    private val playbackRepository: PlaybackRepository by inject()
    private val playlistRepository: PlaylistRepository by inject()

    private val trackSelector: TrackSelector? by lazy {
        if (subtitlesPrefs != null && audioPrefs != null) {
            TrackSelector(subtitlesPrefs, audioPrefs)
        } else null
    }

    private val subtitleAddMutex = Mutex()

    private val _paused = MutableStateFlow<Boolean?>(false)
    val paused: StateFlow<Boolean?> = _paused.asStateFlow()

    private val _precisePosition = MutableStateFlow(0f) // In seconds
    val precisePosition: StateFlow<Float> = _precisePosition.asStateFlow()

    private val _preciseDuration = MutableStateFlow(0f) // In seconds
    val preciseDuration: StateFlow<Float> = _preciseDuration.asStateFlow()

    private val _controlsShown = MutableStateFlow(true)
    val controlsShown: StateFlow<Boolean> = _controlsShown.asStateFlow()

    private val _videoTitle = MutableStateFlow("Video Player")
    val videoTitle: StateFlow<String> = _videoTitle.asStateFlow()

    // Brightness state (0.0f to 1.0f)
    private val _currentBrightness = MutableStateFlow(0.5f)
    val currentBrightness: StateFlow<Float> = _currentBrightness.asStateFlow()

    private val _isBrightnessSliderShown = MutableStateFlow(false)
    val isBrightnessSliderShown: StateFlow<Boolean> = _isBrightnessSliderShown.asStateFlow()

    // Volume state (0.0f to 100.0f percent)
    private val _currentVolumePercent = MutableStateFlow(50f)
    val currentVolumePercent: StateFlow<Float> = _currentVolumePercent.asStateFlow()

    private val _isVolumeSliderShown = MutableStateFlow(false)
    val isVolumeSliderShown: StateFlow<Boolean> = _isVolumeSliderShown.asStateFlow()

    // Drag Seek Overlay State
    private val _dragSeekState = MutableStateFlow<SeekState?>(null)
    val dragSeekState: StateFlow<SeekState?> = _dragSeekState.asStateFlow()

    // Double Tap Seek State
    private val _doubleTapSeekState = MutableStateFlow<DoubleTapSeekState?>(null)
    val doubleTapSeekState: StateFlow<DoubleTapSeekState?> = _doubleTapSeekState.asStateFlow()

    // Sleep Timer
    private val _remainingTime = MutableStateFlow(0) // In seconds
    val remainingTime: StateFlow<Int> = _remainingTime.asStateFlow()

    // Subtitle & Track States
    private val _subtitleTracks = MutableStateFlow<List<TrackNode>>(emptyList())
    val subtitleTracks: StateFlow<List<TrackNode>> = _subtitleTracks.asStateFlow()

    private val _audioTracks = MutableStateFlow<List<TrackNode>>(emptyList())
    val audioTracks: StateFlow<List<TrackNode>> = _audioTracks.asStateFlow()

    private val _currentSubText = MutableStateFlow<String?>(null)
    val currentSubText: StateFlow<String?> = _currentSubText.asStateFlow()

    private val _selectedSubId = MutableStateFlow<Int?>(0)
    val selectedSubId: StateFlow<Int?> = _selectedSubId.asStateFlow()

    private val _selectedSecondarySubId = MutableStateFlow<Int?>(0)
    val selectedSecondarySubId: StateFlow<Int?> = _selectedSecondarySubId.asStateFlow()

    private val _selectedAudioId = MutableStateFlow<Int?>(0)
    val selectedAudioId: StateFlow<Int?> = _selectedAudioId.asStateFlow()
    val currentAudioId: StateFlow<Int> = _selectedAudioId
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _currentDecoder = MutableStateFlow(Decoder.HW_PLUS)
    val currentDecoder: StateFlow<Decoder> = _currentDecoder.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _chapters = MutableStateFlow<List<ChapterNode>>(emptyList())
    val chapters: StateFlow<List<ChapterNode>> = _chapters.asStateFlow()

    private val _currentChapterIndex = MutableStateFlow<Int?>(null)
    val currentChapterIndex: StateFlow<Int?> = _currentChapterIndex.asStateFlow()
    val currentChapter: StateFlow<Int?> = _currentChapterIndex.asStateFlow()

    private val _sheetShown = MutableStateFlow<Sheets>(Sheets.None)
    val sheetShown: StateFlow<Sheets> = _sheetShown.asStateFlow()

    // Playlist & Progress States
    private val _currentVideoId = MutableStateFlow<String?>(null)
    val currentVideoId: StateFlow<String?> = _currentVideoId.asStateFlow()

    private val _playlistItems = MutableStateFlow<List<VideoItem>>(emptyList())
    val playlistItems: StateFlow<List<VideoItem>> = _playlistItems.asStateFlow()

    private val _currentPlaylistIndex = MutableStateFlow(0)
    val currentPlaylistIndex: StateFlow<Int> = _currentPlaylistIndex.asStateFlow()

    private val _isPlaylistMode = MutableStateFlow(false)
    val isPlaylistMode: StateFlow<Boolean> = _isPlaylistMode.asStateFlow()

    private val _resumePositionSec = MutableStateFlow<Double?>(null)
    val resumePositionSec: StateFlow<Double?> = _resumePositionSec.asStateFlow()

    private var autoSaveProgressJob: Job? = null
    private val _externalSubtitles = mutableListOf<String>()
    private var hasAttemptedAutoSelectSub = false

    private var sleepTimerJob: Job? = null
    private var seekCoalescingJob: Job? = null
    private var pollingJob: Job? = null
    private var brightnessHideJob: Job? = null
    private var volumeHideJob: Job? = null
    private var doubleTapHideJob: Job? = null

    private var seekStartPositionSec: Float = 0f
    private var cumulativeSeekDeltaSec: Float = 0f

    init {
        startAdaptivePolling()
        observePreferences()
        startAutoSaveProgress()
    }

    private fun startAutoSaveProgress() {
        autoSaveProgressJob?.cancel()
        autoSaveProgressJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(5000)
                if (_paused.value == false) {
                    saveCurrentProgress()
                }
            }
        }
    }

    private suspend fun saveCurrentProgress() {
        val videoId = _currentVideoId.value ?: return
        val posMs = (_precisePosition.value * 1000f).toLong()
        val durMs = (_preciseDuration.value * 1000f).toLong()

        if (durMs > 0 && posMs >= 0) {
            val isCompleted = (posMs.toFloat() / durMs.toFloat()) > 0.95f
            try {
                playbackRepository.saveProgress(
                    PlaybackProgress(
                        videoId = videoId,
                        positionMs = posMs,
                        durationMs = durMs,
                        lastPlayedTimestamp = System.currentTimeMillis(),
                        isCompleted = isCompleted
                    )
                )
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error saving progress", e)
            }
        }
    }

    fun setPlaylist(items: List<VideoItem>, startIndex: Int = 0) {
        _playlistItems.value = items
        _isPlaylistMode.value = items.isNotEmpty()
        if (items.isNotEmpty() && startIndex in items.indices) {
            _currentPlaylistIndex.value = startIndex
            playPlaylistItem(startIndex)
        }
    }

    fun playPlaylistItem(index: Int) {
        val items = _playlistItems.value
        if (index in items.indices) {
            _currentPlaylistIndex.value = index
            val item = items[index]
            _currentVideoId.value = item.id
            _videoTitle.value = item.title
            mpvController.play(item.uri)
            checkSavedProgress(item.id)
        }
    }

    fun playNextVideo() {
        val items = _playlistItems.value
        if (items.isNotEmpty()) {
            val nextIndex = (_currentPlaylistIndex.value + 1) % items.size
            playPlaylistItem(nextIndex)
        }
    }

    fun playPreviousVideo() {
        val items = _playlistItems.value
        if (items.isNotEmpty()) {
            val prevIndex = if (_currentPlaylistIndex.value - 1 < 0) items.size - 1 else _currentPlaylistIndex.value - 1
            playPlaylistItem(prevIndex)
        }
    }

    fun reorderPlaylist(fromIndex: Int, toIndex: Int) {
        val currentList = _playlistItems.value.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            _playlistItems.value = currentList
            if (_currentPlaylistIndex.value == fromIndex) {
                _currentPlaylistIndex.value = toIndex
            } else if (_currentPlaylistIndex.value in (fromIndex + 1)..toIndex) {
                _currentPlaylistIndex.value -= 1
            } else if (_currentPlaylistIndex.value in toIndex until fromIndex) {
                _currentPlaylistIndex.value += 1
            }
        }
    }

    fun setCurrentVideoDetails(id: String, title: String) {
        _currentVideoId.value = id
        _videoTitle.value = title
        checkSavedProgress(id)
    }

    fun checkSavedProgress(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progress = playbackRepository.getProgress(videoId).first()
                if (progress != null && !progress.isCompleted && progress.positionMs > 10000L) {
                    _resumePositionSec.value = progress.positionMs / 1000.0
                } else {
                    _resumePositionSec.value = null
                }
            } catch (e: Exception) {
                _resumePositionSec.value = null
            }
        }
    }

    fun clearResumePosition() {
        _resumePositionSec.value = null
    }

    private fun observePreferences() {
        playerPrefs?.let { prefs ->
            viewModelScope.launch {
                prefs.defaultSpeed.flow.collect { speed ->
                    if (abs(speed - _playbackSpeed.value) > 0.01f) {
                        setPlaybackSpeed(speed)
                    }
                }
            }
        }
        subtitlesPrefs?.let { prefs ->
            viewModelScope.launch {
                prefs.fontSize.flow.collect { fontSize ->
                    mpvController.setPropertyInt("sub-font-size", fontSize)
                }
            }
            viewModelScope.launch {
                prefs.subScale.flow.collect { scale ->
                    mpvController.setPropertyFloat("sub-scale", scale)
                }
            }
            viewModelScope.launch {
                prefs.bold.flow.collect { isBold ->
                    mpvController.setPropertyBoolean("sub-bold", isBold)
                }
            }
        }
        audioPrefs?.let { prefs ->
            viewModelScope.launch {
                prefs.defaultAudioDelay.flow.collect { delay ->
                    mpvController.setPropertyInt("audio-delay", delay)
                }
            }
        }
        decoderPrefs?.let { prefs ->
            viewModelScope.launch {
                prefs.tryHWDecoding.flow.collect { tryHW ->
                    if (tryHW && _currentDecoder.value == Decoder.SOFTWARE) {
                        setDecoder(Decoder.HW_PLUS)
                    }
                }
            }
        }
    }

    fun setVideoTitle(title: String) {
        _videoTitle.value = title
    }

    fun onFileLoaded(fileName: String) {
        hasAttemptedAutoSelectSub = false
        _videoTitle.value = fileName
    }

    fun updateTracks() {
        val allTracks = mpvController.getTracks()
        val subs = allTracks.filter { it.type == "sub" }
        val audios = allTracks.filter { it.type == "audio" }

        _subtitleTracks.value = subs
        _audioTracks.value = audios

        _selectedSubId.value = mpvController.getCurrentSid()
        _selectedSecondarySubId.value = mpvController.getCurrentSecondarySid()
        _selectedAudioId.value = mpvController.getCurrentAid()

        // Auto select best sub if not done yet
        if (!hasAttemptedAutoSelectSub && subs.isNotEmpty()) {
            hasAttemptedAutoSelectSub = true
            viewModelScope.launch {
                trackSelector?.onFileLoaded(false, mpvController)
            }
        }
    }

    fun selectAudioTrack(id: Int) {
        mpvController.selectAudioTrack(id)
        _selectedAudioId.value = id
    }

    fun addAudio(uri: Uri, context: Context) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            Log.e("PlayerViewModel", "Could not take persistable permission for audio", e)
        }

        val audioPath = uri.toString()
        mpvController.addAudio(audioPath)
        updateTracks()
    }

    fun setDecoder(decoder: Decoder) {
        mpvController.setDecoder(decoder.value)
        _currentDecoder.value = decoder
    }

    fun updateDecoder(decoder: Decoder) {
        setDecoder(decoder)
    }

    fun setPlaybackSpeed(speed: Float) {
        val rounded = (speed.coerceIn(0.25f, 4.0f) * 100).toInt() / 100f
        mpvController.setPlaybackSpeed(rounded)
        _playbackSpeed.value = rounded
    }

    fun selectChapter(index: Int) {
        mpvController.selectChapter(index)
        _currentChapterIndex.value = index
    }

    fun seekToChapter(index: Int) {
        selectChapter(index)
        unpause()
    }

    fun unpause() {
        mpvController.resume()
        _paused.value = false
    }

    fun openSheet(sheet: Sheets) {
        _sheetShown.update { sheet }
        if (sheet != Sheets.None) setControlsShown(false)
    }

    fun closeSheet() {
        _sheetShown.update { Sheets.None }
        setControlsShown(true)
    }

    fun addSubtitle(uri: Uri, context: Context, select: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            subtitleAddMutex.withLock {
                val subPath = uri.toString()
                if (_externalSubtitles.contains(subPath)) return@withLock

                if (uri.scheme == "content") {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        Log.e("PlayerViewModel", "Could not take persistable permission", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    mpvController.addSubtitle(subPath, select)
                    updateTracks()
                }
                _externalSubtitles.add(subPath)
            }
        }
    }

    fun removeSubtitle(id: Int) {
        mpvController.getAttachedView()?.command(arrayOf("sub-remove", id.toString()))
        updateTracks()
    }

    fun onVideoFileLoaded(hasState: Boolean = false) {
        hasAttemptedAutoSelectSub = false
        viewModelScope.launch {
            trackSelector?.onFileLoaded(hasState, mpvController)
            updateTracks()
        }
    }

    fun toggleSubtitle(id: Int) {
        val currentP = _selectedSubId.value ?: 0
        val currentS = _selectedSecondarySubId.value ?: 0

        when {
            id == currentP -> {
                // Turn off primary
                mpvController.setPrimarySubtitle(0)
                _selectedSubId.value = 0
            }
            id == currentS -> {
                // Turn off secondary
                mpvController.setSecondarySubtitle(0)
                _selectedSecondarySubId.value = 0
            }
            currentP == 0 -> {
                // Set as primary
                mpvController.setPrimarySubtitle(id)
                _selectedSubId.value = id
            }
            currentS == 0 -> {
                // Set as secondary
                mpvController.setSecondarySubtitle(id)
                _selectedSecondarySubId.value = id
            }
            else -> {
                // Replace primary
                mpvController.setPrimarySubtitle(id)
                _selectedSubId.value = id
            }
        }
    }

    fun disableSubtitles() {
        mpvController.setPrimarySubtitle(0)
        mpvController.setSecondarySubtitle(0)
        _selectedSubId.value = 0
        _selectedSecondarySubId.value = 0
    }

    fun isSubtitleSelected(id: Int): Boolean {
        return id == _selectedSubId.value || id == _selectedSecondarySubId.value
    }

    fun subtitleSelectionIndicator(id: Int): String? {
        return when (id) {
            _selectedSubId.value -> "P"
            _selectedSecondarySubId.value -> "S"
            else -> null
        }
    }

    fun toggleControls() {
        _controlsShown.update { !it }
    }

    fun setControlsShown(shown: Boolean) {
        _controlsShown.value = shown
    }

    fun pauseUnpause() {
        mpvController.togglePlayPause()
        val currentIsPlaying = mpvController.playerState.value.isPlaying
        _paused.value = !currentIsPlaying
    }

    fun play() {
        mpvController.resume()
        _paused.value = false
    }

    fun pause() {
        mpvController.pause()
        _paused.value = true
    }

    fun seekBy(offsetSeconds: Int) {
        mpvController.seekBy(offsetSeconds)
        val posSeconds = mpvController.playerState.value.positionMs / 1000f
        _precisePosition.value = posSeconds
    }

    fun leftSeek() {
        seekBy(-10)
        showDoubleTapFeedback(isLeft = true)
    }

    fun rightSeek() {
        seekBy(10)
        showDoubleTapFeedback(isLeft = false)
    }

    private fun showDoubleTapFeedback(isLeft: Boolean) {
        doubleTapHideJob?.cancel()
        _doubleTapSeekState.value = DoubleTapSeekState(
            isLeft = isLeft,
            amountSeconds = 10,
            timestamp = System.currentTimeMillis()
        )
        doubleTapHideJob = viewModelScope.launch {
            delay(800)
            _doubleTapSeekState.value = null
        }
    }

    fun seekTo(positionSeconds: Float) {
        _precisePosition.value = positionSeconds
        seekCoalescingJob?.cancel()
        seekCoalescingJob = viewModelScope.launch {
            delay(60) // Coalescing delay
            val targetMs = (positionSeconds * 1000).toLong()
            mpvController.seekTo(targetMs)
        }
    }

    // --- BRIGHTNESS CONTROLS ---
    fun initBrightness(window: Window?, context: Context) {
        val currentVal = window?.attributes?.screenBrightness
        if (currentVal != null && currentVal >= 0f) {
            _currentBrightness.value = currentVal.coerceIn(0.01f, 1f)
        } else {
            try {
                val sysVal = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    128
                )
                _currentBrightness.value = (sysVal / 255f).coerceIn(0.01f, 1f)
            } catch (e: Exception) {
                _currentBrightness.value = 0.5f
            }
        }
    }

    fun changeBrightnessBy(deltaPx: Float, window: Window?, context: Context) {
        // Every 10px = 0.05 brightness change
        val deltaValue = (deltaPx / 10f) * 0.05f
        val newBrightness = (_currentBrightness.value + deltaValue).coerceIn(0.01f, 1.0f)
        _currentBrightness.value = newBrightness

        window?.let {
            val lp = it.attributes
            lp.screenBrightness = newBrightness
            it.attributes = lp
        }

        try {
            val val255 = (newBrightness * 255).toInt().coerceIn(1, 255)
            if (Settings.System.canWrite(context)) {
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, val255)
            }
        } catch (e: Exception) {
            // Permission or write error ignored
        }

        displayBrightnessSlider()
    }

    fun displayBrightnessSlider() {
        _isBrightnessSliderShown.value = true
        brightnessHideJob?.cancel()
        brightnessHideJob = viewModelScope.launch {
            delay(1000)
            _isBrightnessSliderShown.value = false
        }
    }

    // --- VOLUME CONTROLS ---
    fun initVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        _currentVolumePercent.value = (currentVol.toFloat() / maxVol.toFloat() * 100f).coerceIn(0f, 100f)
    }

    fun changeVolumeBy(deltaPx: Float, context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val stepPercent = 100f / maxVol.toFloat()

        // Every 10px = 1 volume step
        val steps = (deltaPx / 10f)
        val newPercent = (_currentVolumePercent.value + (steps * stepPercent)).coerceIn(0f, 100f)
        _currentVolumePercent.value = newPercent

        val targetVol = ((newPercent / 100f) * maxVol).toInt().coerceIn(0, maxVol)
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
        } catch (e: Exception) {
            Log.e("PlayerViewModel", "Error setting volume", e)
        }

        displayVolumeSlider()
    }

    fun displayVolumeSlider() {
        _isVolumeSliderShown.value = true
        volumeHideJob?.cancel()
        volumeHideJob = viewModelScope.launch {
            delay(1000)
            _isVolumeSliderShown.value = false
        }
    }

    // --- HORIZONTAL DRAG SEEK CONTROLS ---
    fun onHorizontalDragStart() {
        seekStartPositionSec = _precisePosition.value
        cumulativeSeekDeltaSec = 0f
        _dragSeekState.value = SeekState(
            targetPositionSec = seekStartPositionSec,
            diffSeconds = 0f,
            isForwards = true,
            isDragging = true
        )
    }

    fun onHorizontalDrag(deltaPx: Float, screenWidthPx: Float) {
        val duration = _preciseDuration.value.coerceAtLeast(1f)
        // Full screen width = Total duration
        // If duration > 30 mins (1800s), reduce sensitivity by factor 0.5
        val sensitivityFactor = if (duration > 1800f) 0.5f else 1.0f
        val pxToSecRatio = (duration / screenWidthPx.coerceAtLeast(1f)) * sensitivityFactor

        val deltaSec = deltaPx * pxToSecRatio
        cumulativeSeekDeltaSec += deltaSec

        val newTargetSec = (seekStartPositionSec + cumulativeSeekDeltaSec).coerceIn(0f, duration)
        val actualDiffSec = newTargetSec - seekStartPositionSec

        _dragSeekState.value = SeekState(
            targetPositionSec = newTargetSec,
            diffSeconds = actualDiffSec,
            isForwards = actualDiffSec >= 0,
            isDragging = true
        )
    }

    fun onHorizontalDragEnd() {
        val finalState = _dragSeekState.value
        if (finalState != null) {
            seekTo(finalState.targetPositionSec)
        }
        _dragSeekState.value = null
    }

    fun startTimer(seconds: Int) {
        sleepTimerJob?.cancel()
        _remainingTime.value = seconds
        if (seconds <= 0) return

        sleepTimerJob = viewModelScope.launch {
            while (isActive && _remainingTime.value > 0) {
                delay(1000)
                _remainingTime.update { current ->
                    val next = current - 1
                    if (next <= 0) {
                        pause()
                        0
                    } else {
                        next
                    }
                }
            }
        }
    }

    fun cancelTimer() {
        sleepTimerJob?.cancel()
        _remainingTime.value = 0
    }

    private fun startAdaptivePolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                val state = mpvController.playerState.value
                val posSec = state.positionMs / 1000f
                val durSec = state.durationMs / 1000f

                _precisePosition.value = posSec
                _preciseDuration.value = durSec
                _paused.value = !state.isPlaying

                // Poll subtitle text, tracks, decoder, speed, chapters
                _currentSubText.value = mpvController.getSubtitleText()
                updateTracks()
                _currentDecoder.value = Decoder.getDecoderFromValue(mpvController.getCurrentDecoderValue())
                _playbackSpeed.value = mpvController.getPlaybackSpeed()
                _chapters.value = mpvController.getChapters()
                _currentChapterIndex.value = mpvController.getCurrentChapterIndex()

                // Adaptive delay: 50ms while controls are shown or seeking, 500ms otherwise
                val pollInterval = if (_controlsShown.value || seekCoalescingJob?.isActive == true) 50L else 500L
                delay(pollInterval)
            }
        }
    }

    override fun onCleared() {
        pollingJob?.cancel()
        sleepTimerJob?.cancel()
        seekCoalescingJob?.cancel()
        brightnessHideJob?.cancel()
        volumeHideJob?.cancel()
        doubleTapHideJob?.cancel()
        autoSaveProgressJob?.cancel()
        super.onCleared()
    }
}
