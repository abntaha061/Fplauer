package com.finalplayer.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.finalplayer.app.data.preferences.base.DataStorePreference
import com.finalplayer.app.data.preferences.base.Preference

class SubtitlesPreferences(private val dataStore: DataStore<Preferences>) {
    companion object {
        val PREFERRED_LANGS = stringPreferencesKey("sub_preferred_langs")
        val AUTO_ENABLE = booleanPreferencesKey("sub_auto_enable")
        val FONT_SIZE = intPreferencesKey("sub_font_size")
        val BOLD = booleanPreferencesKey("sub_bold")
        val ITALIC = booleanPreferencesKey("sub_italic")
        val SUB_SCALE = floatPreferencesKey("sub_scale")
        val SUB_POS = intPreferencesKey("sub_pos")
        val BORDER_SIZE = floatPreferencesKey("sub_border_size")
        val OVERRIDE_ASS = booleanPreferencesKey("sub_override_ass")
        val DEFAULT_DELAY = intPreferencesKey("sub_default_delay_ms")
        val DEFAULT_SPEED = floatPreferencesKey("sub_default_speed")
        val JUSTIFICATION = stringPreferencesKey("sub_justification")
        val TEXT_COLOR = longPreferencesKey("sub_text_color")
        val BORDER_COLOR = longPreferencesKey("sub_border_color")
        val SHADOW_COLOR = longPreferencesKey("sub_shadow_color")
        val BG_COLOR = longPreferencesKey("sub_bg_color")
        val SCALE_BY_WINDOW = booleanPreferencesKey("sub_scale_by_window")
        val FONT = stringPreferencesKey("sub_font")
        val SHADOW_OFFSET = intPreferencesKey("sub_shadow_offset")
    }

    val preferredLanguages   = pref(PREFERRED_LANGS, "eng,en")
    val autoEnableSubtitles  = pref(AUTO_ENABLE, true)
    val fontSize             = pref(FONT_SIZE, 55)
    val bold                 = pref(BOLD, false)
    val italic               = pref(ITALIC, false)
    val subScale             = pref(SUB_SCALE, 1.0f)
    val subPos               = pref(SUB_POS, 95)
    val borderSize           = pref(BORDER_SIZE, 3.0f)
    val overrideAssSubs      = pref(OVERRIDE_ASS, false)
    val defaultSubDelay      = pref(DEFAULT_DELAY, 0)
    val defaultSubSpeed      = pref(DEFAULT_SPEED, 1.0f)
    val justification        = pref(JUSTIFICATION, "center")
    val textColor            = pref(TEXT_COLOR, 0xFFFFFFFFL)
    val borderColor          = pref(BORDER_COLOR, 0xFF000000L)
    val shadowColor          = pref(SHADOW_COLOR, 0x80000000L)
    val backgroundColor      = pref(BG_COLOR, 0x00000000L)
    val scaleByWindow        = pref(SCALE_BY_WINDOW, true)
    val font                 = pref(FONT, "")
    val shadowOffset         = pref(SHADOW_OFFSET, 0)

    private fun <T> pref(key: Preferences.Key<T>, default: T): Preference<T> =
        DataStorePreference(dataStore, key, default)
}
