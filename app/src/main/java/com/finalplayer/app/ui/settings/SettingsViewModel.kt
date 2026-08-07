package com.finalplayer.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.data.preferences.AppearancePreferences
import com.finalplayer.app.data.preferences.AudioPreferences
import com.finalplayer.app.data.preferences.DecoderPreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import com.finalplayer.app.data.preferences.base.Preference
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
    val defaultSpeed = playerPrefs.defaultSpeed.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, 1.0f)
    val savePositionOnQuit = playerPrefs.savePositionOnQuit.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val usePreciseSeeking = playerPrefs.usePreciseSeeking.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val autoPlayNext = playerPrefs.autoPlayNext.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val playerTimeToDisappear = playerPrefs.playerTimeToDisappear.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, 3000)
    val showSystemStatusBar = playerPrefs.showSystemStatusBar.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)

    val doubleTapToSeekDuration = playerPrefs.doubleTapToSeekDuration.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, 10)
    val showDoubleTapOvals = playerPrefs.showDoubleTapOvals.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val showSeekTimeWhileSeeking = playerPrefs.showSeekTimeWhileSeeking.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val swapVolumeAndBrightness = playerPrefs.swapVolumeAndBrightness.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val holdForMultipleSpeed = playerPrefs.holdForMultipleSpeed.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, 2.0f)

    val showLoadingCircle = playerPrefs.showLoadingCircle.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val invertDuration = playerPrefs.invertDuration.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val showBufferedRange = playerPrefs.showBufferedRange.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val showChapterIndicators = playerPrefs.showChapterIndicators.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val autoPiPOnNavigation = playerPrefs.autoPiPOnNavigation.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)

    // Appearance Preferences State
    val hidePlayerButtonsBackground = appearancePrefs.hidePlayerButtonsBackground.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val glassmorphismControls = appearancePrefs.glassmorphismControls.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val glassmorphismSeekbar = appearancePrefs.glassmorphismSeekbar.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val useSpringAnimations = appearancePrefs.useSpringAnimations.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)
    val matchControlsToTheme = appearancePrefs.matchControlsToTheme.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, false)
    val playerAlwaysDark = appearancePrefs.playerAlwaysDark.asFlow().stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun <T> setPreference(preference: Preference<T>, value: T) {
        viewModelScope.launch {
            preference.set(value)
        }
    }
}
