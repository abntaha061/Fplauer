package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class DecoderPreferences(dataStore: DataStore<Preferences>) {
    val tryHWDecoding = Preference(dataStore, booleanPreferencesKey("try_hw_decoding"), true)
    val gpuNext = Preference(dataStore, booleanPreferencesKey("gpu_next"), false)
    val profile = Preference(dataStore, stringPreferencesKey("decoder_profile"), "fast")
    val debanding = Preference(dataStore, stringPreferencesKey("decoder_debanding"), "None")
}
