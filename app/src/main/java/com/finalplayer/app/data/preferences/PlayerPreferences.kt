package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

class PlayerPreferences(dataStore: DataStore<Preferences>) {
    // تشغيل
    val defaultSpeed = Preference(dataStore, floatPreferencesKey("default_speed"), 1.0f)
    val savePositionOnQuit = Preference(dataStore, booleanPreferencesKey("save_position_on_quit"), true)
    val usePreciseSeeking = Preference(dataStore, booleanPreferencesKey("use_precise_seeking"), false)
    val autoPlayNext = Preference(dataStore, booleanPreferencesKey("auto_play_next"), true)
    val playerTimeToDisappear = Preference(dataStore, intPreferencesKey("player_time_to_disappear"), 3000)
    val showSystemStatusBar = Preference(dataStore, booleanPreferencesKey("show_system_status_bar"), false)

    // إيماءات
    val doubleTapToSeekDuration = Preference(dataStore, intPreferencesKey("double_tap_to_seek_duration"), 10)
    val showDoubleTapOvals = Preference(dataStore, booleanPreferencesKey("show_double_tap_ovals"), true)
    val showSeekTimeWhileSeeking = Preference(dataStore, booleanPreferencesKey("show_seek_time_while_seeking"), true)
    val swapVolumeAndBrightness = Preference(dataStore, booleanPreferencesKey("swap_volume_and_brightness"), false)
    val holdForMultipleSpeed = Preference(dataStore, floatPreferencesKey("hold_for_multiple_speed"), 2.0f)

    // واجهة المشغل
    val showLoadingCircle = Preference(dataStore, booleanPreferencesKey("show_loading_circle"), true)
    val invertDuration = Preference(dataStore, booleanPreferencesKey("invert_duration"), false)
    val showBufferedRange = Preference(dataStore, booleanPreferencesKey("show_buffered_range"), true)
    val showChapterIndicators = Preference(dataStore, booleanPreferencesKey("show_chapter_indicators"), true)
    val showSeekbarWhenPaused = Preference(dataStore, booleanPreferencesKey("show_seekbar_when_paused"), true)
    val autoPiPOnNavigation = Preference(dataStore, booleanPreferencesKey("auto_pip_on_navigation"), true)
    val rememberBrightness = Preference(dataStore, booleanPreferencesKey("remember_brightness"), false)
    val defaultBrightness = Preference(dataStore, floatPreferencesKey("default_brightness"), -1f)
}
