package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

class SortPreferences(dataStore: DataStore<Preferences>) {
    val sortBy = Preference(dataStore, stringPreferencesKey("sort_by"), "title")
    val sortAscending = Preference(dataStore, booleanPreferencesKey("sort_ascending"), true)
    val viewMode = Preference(dataStore, stringPreferencesKey("view_mode"), "folder")
    val layoutMode = Preference(dataStore, stringPreferencesKey("layout_mode"), "list")
    val visibleFields = Preference(dataStore, stringSetPreferencesKey("visible_fields"), setOf("Path", "Folder Size", "Total Media"))
    val onlyForFolderList = Preference(dataStore, booleanPreferencesKey("only_for_folder_list"), false)
}
