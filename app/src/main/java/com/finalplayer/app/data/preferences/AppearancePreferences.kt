package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.finalplayer.app.data.preferences.base.DataStorePreference
import com.finalplayer.app.data.preferences.base.Preference

class AppearancePreferences(private val dataStore: DataStore<Preferences>) {
    val hidePlayerButtonsBackground = pref(booleanPreferencesKey("app_hide_btn_bg"), false)
    val seekbarStyle                = pref(stringPreferencesKey("app_seekbar_style"), "default")
    val useSpringAnimations         = pref(booleanPreferencesKey("app_spring_anim"), true)
    val matchControlsToTheme        = pref(booleanPreferencesKey("app_match_theme"), false)
    val playerAlwaysDark            = pref(booleanPreferencesKey("app_always_dark"), true)
    val glassmorphismControls       = pref(booleanPreferencesKey("app_glass_ctrl"), false)
    val glassmorphismSeekbar        = pref(booleanPreferencesKey("app_glass_seek"), false)

    private fun <T> pref(key: Preferences.Key<T>, default: T): Preference<T> =
        DataStorePreference(dataStore, key, default)
}
