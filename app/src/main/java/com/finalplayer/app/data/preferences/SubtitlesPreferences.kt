package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class SubtitlesPreferences(dataStore: DataStore<Preferences>) {
    val preferredLanguages = Preference(dataStore, stringPreferencesKey("sub_preferred_languages"), "eng,en")
    val autoEnableSubtitles = Preference(dataStore, booleanPreferencesKey("auto_enable_subtitles"), true)
    val fontSize = Preference(dataStore, intPreferencesKey("sub_font_size"), 55)
    val bold = Preference(dataStore, booleanPreferencesKey("sub_bold"), false)
    val subScale = Preference(dataStore, floatPreferencesKey("sub_scale"), 1.0f)
    val subPos = Preference(dataStore, intPreferencesKey("sub_pos"), 100)
    val borderSize = Preference(dataStore, floatPreferencesKey("sub_border_size"), 3.0f)
    val overrideAssSubs = Preference(dataStore, booleanPreferencesKey("override_ass_subs"), false)
    val defaultSubDelay = Preference(dataStore, intPreferencesKey("default_sub_delay"), 0)
    val autoLoadSubtitles = Preference(dataStore, booleanPreferencesKey("auto_load_subtitles"), true)
}
