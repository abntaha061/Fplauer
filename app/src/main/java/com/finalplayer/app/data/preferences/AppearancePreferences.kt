package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class AppearancePreferences(dataStore: DataStore<Preferences>) {
    val hidePlayerButtonsBackground = Preference(dataStore, booleanPreferencesKey("hide_player_buttons_background"), false)
    val seekbarStyle = Preference(dataStore, stringPreferencesKey("seekbar_style"), "default")
    val useSpringAnimations = Preference(dataStore, booleanPreferencesKey("use_spring_animations"), true)
    val matchControlsToTheme = Preference(dataStore, booleanPreferencesKey("match_controls_to_theme"), false)
    val playerAlwaysDark = Preference(dataStore, booleanPreferencesKey("player_always_dark"), true)
    val glassmorphismControls = Preference(dataStore, booleanPreferencesKey("glassmorphism_controls"), false)
    val glassmorphismSeekbar = Preference(dataStore, booleanPreferencesKey("glassmorphism_seekbar"), false)
}
