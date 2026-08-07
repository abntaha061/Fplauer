package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.finalplayer.app.data.preferences.AudioPreferences
import com.finalplayer.app.player.core.MPVLib
import com.finalplayer.app.ui.settings.components.EditTextPreferenceItem
import com.finalplayer.app.ui.settings.components.SettingsSectionHeader
import com.finalplayer.app.ui.settings.components.SliderPreferenceItem
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem
import org.koin.compose.koinInject

@Composable
fun AudioSettingsTab() {
    val prefs: AudioPreferences = koinInject()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsSectionHeader("عام") }
        item {
            var langs by remember { mutableStateOf(prefs.preferredLanguages.get()) }
            EditTextPreferenceItem(
                title = "اللغات المفضلة",
                subtitle = if (langs.isBlank()) "غير محدد" else langs,
                value = langs,
                onValueChange = {
                    langs = it
                    prefs.preferredLanguages.set(it)
                }
            )
        }
        item {
            var pitchCorrection by remember {
                mutableStateOf(prefs.audioPitchCorrection.get())
            }
            SwitchPreferenceItem(
                title = "تصحيح النغمة",
                subtitle = "الحفاظ على نغمة الصوت عند تغيير السرعة",
                checked = pitchCorrection,
                onCheckedChange = {
                    pitchCorrection = it
                    prefs.audioPitchCorrection.set(it)
                    MPVLib.setOptionString(
                        "audio-pitch-correction", if (it) "yes" else "no")
                }
            )
        }
        item {
            var volBoost by remember { mutableIntStateOf(prefs.volumeBoostCap.get()) }
            SliderPreferenceItem(
                title = "حد تضخيم الصوت",
                subtitle = "الحد الأقصى لتضخيم الصوت فوق 100%: $volBoost%",
                value = volBoost.toFloat(),
                range = 0f..200f,
                steps = 19,
                onValueChangeFinished = {
                    volBoost = it.toInt()
                    prefs.volumeBoostCap.set(it.toInt())
                    MPVLib.setPropertyString(
                        "volume-max", (100 + it.toInt()).toString())
                }
            )
        }
        item {
            var norm by remember { mutableStateOf(prefs.volumeNormalization.get()) }
            SwitchPreferenceItem(
                title = "تطبيع مستوى الصوت",
                subtitle = "تقليل التذبذب في مستوى الصوت (dynaudnorm)",
                checked = norm,
                onCheckedChange = {
                    norm = it
                    prefs.volumeNormalization.set(it)
                }
            )
        }
        item {
            var drc by remember { mutableStateOf(prefs.drcEnabled.get()) }
            SwitchPreferenceItem(
                title = "ضغط النطاق الديناميكي",
                subtitle = "تقليل الفرق بين الأصوات الهادئة والعالية (وضع الليل)",
                checked = drc,
                onCheckedChange = {
                    drc = it
                    prefs.drcEnabled.set(it)
                }
            )
        }
    }
}
