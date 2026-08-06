package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class AudioPreferences(dataStore: DataStore<Preferences>) {
    val preferredLanguages = Preference(dataStore, stringPreferencesKey("audio_preferred_languages"), "")
    val defaultAudioDelay = Preference(dataStore, intPreferencesKey("default_audio_delay"), 0)
    val audioPitchCorrection = Preference(dataStore, booleanPreferencesKey("audio_pitch_correction"), true)
    val volumeBoostCap = Preference(dataStore, intPreferencesKey("volume_boost_cap"), 0)
}
