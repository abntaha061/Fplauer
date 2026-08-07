package com.finalplayer.app.data.preferences.base

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStorePreference<T>(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T
) : Preference<T> {
    override fun get(): T = runBlocking {
        dataStore.data.map { it[key] ?: defaultValue }.first()
    }

    override fun set(value: T) {
        runBlocking {
            dataStore.edit { it[key] = value }
        }
    }

    override fun asFlow(): Flow<T> =
        dataStore.data.map { it[key] ?: defaultValue }

    override fun changes(): Flow<T> =
        dataStore.data.map { it[key] ?: defaultValue }.distinctUntilChanged()
}
