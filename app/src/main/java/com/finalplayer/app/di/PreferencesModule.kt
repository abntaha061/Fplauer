package com.finalplayer.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.finalplayer.app.data.preferences.AppearancePreferences
import com.finalplayer.app.data.preferences.AudioPreferences
import com.finalplayer.app.data.preferences.DecoderPreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.data.preferences.SortPreferences
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

val preferencesModule = module {
    single<DataStore<Preferences>> { androidContext().appDataStore }

    single { PlayerPreferences(get()) }
    single { SubtitlesPreferences(get()) }
    single { AudioPreferences(get()) }
    single { DecoderPreferences(get()) }
    single { AppearancePreferences(get()) }
    single { SortPreferences(get()) }
}
