package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.finalplayer.app.data.preferences.base.DataStorePreference
import com.finalplayer.app.data.preferences.base.Preference

class PlayerPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val DEFAULT_SPEED = floatPreferencesKey("default_speed")
        val SAVE_POSITION = booleanPreferencesKey("save_position_on_quit")
        val USE_PRECISE_SEEKING = booleanPreferencesKey("use_precise_seeking")
        val AUTO_PLAY_NEXT = booleanPreferencesKey("auto_play_next")
        val CONTROLS_TIMEOUT_MS = intPreferencesKey("controls_timeout_ms")
        val SHOW_STATUS_BAR = booleanPreferencesKey("show_system_status_bar")
        val SHOW_NAV_BAR = booleanPreferencesKey("show_system_nav_bar")
        val DOUBLE_TAP_SEEK_DURATION = intPreferencesKey("double_tap_seek_duration")
        val SHOW_DOUBLE_TAP_OVALS = booleanPreferencesKey("show_double_tap_ovals")
        val SHOW_SEEK_TIME = booleanPreferencesKey("show_seek_time_while_seeking")
        val SWAP_VOL_BRIGHTNESS = booleanPreferencesKey("swap_volume_brightness")
        val HOLD_SPEED = floatPreferencesKey("hold_for_multiple_speed")
        val SHOW_LOADING_CIRCLE = booleanPreferencesKey("show_loading_circle")
        val INVERT_DURATION = booleanPreferencesKey("invert_duration")
        val SHOW_BUFFERED_RANGE = booleanPreferencesKey("show_buffered_range")
        val SHOW_CHAPTER_INDICATORS = booleanPreferencesKey("show_chapter_indicators")
        val AUTO_PIP = booleanPreferencesKey("auto_pip_on_navigation")
        val REMEMBER_BRIGHTNESS = booleanPreferencesKey("remember_brightness")
        val DEFAULT_BRIGHTNESS = floatPreferencesKey("default_brightness")
        val KEEP_SCREEN_ON_PAUSE = booleanPreferencesKey("keep_screen_on_pause")
        val PLAYLIST_MODE = booleanPreferencesKey("playlist_mode")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val SHUFFLE = booleanPreferencesKey("shuffle_enabled")
        val VIDEO_OPEN_ANIMATION = stringPreferencesKey("video_open_animation")
        val ANIMATION_SPEED = floatPreferencesKey("animation_speed")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val STATS_PAGE = intPreferencesKey("enabled_stats_page")
    }

    val defaultSpeed           = pref(DEFAULT_SPEED, 1.0f)
    val savePositionOnQuit     = pref(SAVE_POSITION, true)
    val usePreciseSeeking      = pref(USE_PRECISE_SEEKING, false)
    val autoPlayNext           = pref(AUTO_PLAY_NEXT, true)
    val playerTimeToDisappear  = pref(CONTROLS_TIMEOUT_MS, 3000)
    val showSystemStatusBar    = pref(SHOW_STATUS_BAR, false)
    val showSystemNavigationBar= pref(SHOW_NAV_BAR, false)
    val doubleTapToSeekDuration= pref(DOUBLE_TAP_SEEK_DURATION, 10)
    val showDoubleTapOvals     = pref(SHOW_DOUBLE_TAP_OVALS, true)
    val showSeekTimeWhileSeeking = pref(SHOW_SEEK_TIME, true)
    val swapVolumeAndBrightness= pref(SWAP_VOL_BRIGHTNESS, false)
    val holdForMultipleSpeed   = pref(HOLD_SPEED, 2.0f)
    val showLoadingCircle      = pref(SHOW_LOADING_CIRCLE, true)
    val invertDuration         = pref(INVERT_DURATION, false)
    val showBufferedRange      = pref(SHOW_BUFFERED_RANGE, true)
    val showChapterIndicators  = pref(SHOW_CHAPTER_INDICATORS, true)
    val autoPiPOnNavigation    = pref(AUTO_PIP, true)
    val rememberBrightness     = pref(REMEMBER_BRIGHTNESS, false)
    val defaultBrightness      = pref(DEFAULT_BRIGHTNESS, -1f)
    val keepScreenOnPause      = pref(KEEP_SCREEN_ON_PAUSE, true)
    val enabledStatisticsPage  = pref(STATS_PAGE, 0)
    val reduceMotion           = pref(REDUCE_MOTION, false)

    private fun <T> pref(key: Preferences.Key<T>, default: T): Preference<T> =
        DataStorePreference(dataStore, key, default)
}
