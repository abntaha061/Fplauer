package com.finalplayer.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.data.preferences.AppearancePreferences
import com.finalplayer.app.data.preferences.AudioPreferences
import com.finalplayer.app.data.preferences.DecoderPreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    val playerPrefs: PlayerPreferences,
    val subtitlesPrefs: SubtitlesPreferences,
    val audioPrefs: AudioPreferences,
    val decoderPrefs: DecoderPreferences,
    val appearancePrefs: AppearancePreferences
) : ViewModel() {

    // Player Preferences State
    val defaultSpeed = playerPrefs.defaultSpeed.flow.stateIn(viewModelScope, SharingStarted.Lazily, 1.0f)
    val savePositionOnQuit = playerPrefs.savePositionOnQuit.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val usePreciseSeeking = playerPrefs.usePreciseSeeking.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val autoPlayNext = playerPrefs.autoPlayNext.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val playerTimeToDisappear = playerPrefs.playerTimeToDisappear.flow.stateIn(viewModelScope, SharingStarted.Lazily, 3000)
    val showSystemStatusBar = playerPrefs.showSystemStatusBar.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val doubleTapToSeekDuration = playerPrefs.doubleTapToSeekDuration.flow.stateIn(viewModelScope, SharingStarted.Lazily, 10)
    val showDoubleTapOvals = playerPrefs.showDoubleTapOvals.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val showSeekTimeWhileSeeking = playerPrefs.showSeekTimeWhileSeeking.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val swapVolumeAndBrightness = playerPrefs.swapVolumeAndBrightness.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val holdForMultipleSpeed = playerPrefs.holdForMultipleSpeed.flow.stateIn(viewModelScope, SharingStarted.Lazily, 2.0f)

    val showLoadingCircle = playerPrefs.showLoadingCircle.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val invertDuration = playerPrefs.invertDuration.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val showBufferedRange = playerPrefs.showBufferedRange.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val showChapterIndicators = playerPrefs.showChapterIndicators.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val showSeekbarWhenPaused = playerPrefs.showSeekbarWhenPaused.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val autoPiPOnNavigation = playerPrefs.autoPiPOnNavigation.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)

    // Appearance Preferences State
    val hidePlayerButtonsBackground = appearancePrefs.hidePlayerButtonsBackground.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val glassmorphismControls = appearancePrefs.glassmorphismControls.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val glassmorphismSeekbar = appearancePrefs.glassmorphismSeekbar.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val useSpringAnimations = appearancePrefs.useSpringAnimations.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val matchControlsToTheme = appearancePrefs.matchControlsToTheme.flow.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val playerAlwaysDark = appearancePrefs.playerAlwaysDark.flow.stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun <T> setPreference(preference: com.finalplayer.app.data.preferences.Preference<T>, value: T) {
        viewModelScope.launch {
            preference.set(value)
        }
    }
}
