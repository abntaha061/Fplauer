package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preference<T>(
    private val dataStore: DataStore<Preferences>,
    val key: Preferences.Key<T>,
    val defaultValue: T
) {
    val flow: Flow<T> = dataStore.data.map { preferences ->
        preferences[key] ?: defaultValue
    }

    suspend fun set(value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}
