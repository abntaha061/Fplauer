package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.finalplayer.app.data.preferences.GesturePreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.ui.settings.components.SettingsSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem
import org.koin.compose.koinInject

@Composable
fun GesturesSettingsTab() {
    val prefs: GesturePreferences = koinInject()
    val playerPrefs: PlayerPreferences = koinInject()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsSectionHeader("الإيماءات") }
        item {
            // Orientation chip — icon فقط بدون toggle
            ListItem(
                headlineContent = { Text("الاتجاه") },
                supportingContent = { Text("الفيديو") },
                leadingContent = {
                    Icon(Icons.Default.ScreenRotation, null)
                }
            )
        }
        item {
            var brightness by remember {
                mutableStateOf(prefs.brightnessGestureEnabled.get())
            }
            SwitchPreferenceItem(
                title = "إيماءات السطوع",
                subtitle = "إيماءات السطوع",
                checked = brightness,
                onCheckedChange = {
                    brightness = it
                    prefs.brightnessGestureEnabled.set(it)
                }
            )
        }
        item {
            var vol by remember { mutableStateOf(prefs.volumeGestureEnabled.get()) }
            SwitchPreferenceItem(
                title = "إيماءات الصوت",
                subtitle = "إيماءات الصوت",
                checked = vol,
                onCheckedChange = {
                    vol = it
                    prefs.volumeGestureEnabled.set(it)
                }
            )
        }
        item {
            var pinch by remember { mutableStateOf(prefs.pinchToZoom.get()) }
            SwitchPreferenceItem(
                title = "قرص للتكبير",
                subtitle = "قرص للتكبير",
                checked = pinch,
                onCheckedChange = {
                    pinch = it
                    prefs.pinchToZoom.set(it)
                }
            )
        }
        item {
            var seek by remember { mutableStateOf(prefs.seekGestureEnabled.get()) }
            SwitchPreferenceItem(
                title = "التمرير الأفقي للتقديم",
                subtitle = "التمرير الأفقي للتقديم",
                checked = seek,
                onCheckedChange = {
                    seek = it
                    prefs.seekGestureEnabled.set(it)
                }
            )
        }
        item {
            var subScroll by remember { mutableStateOf(prefs.subtitleScrollSeek.get()) }
            SwitchPreferenceItem(
                title = "التمرير للتقديم في الترجمة",
                subtitle = "مرّر يساراً أو يميناً في أعلى أو أسفل الشاشة للتقديم في الترجمة",
                checked = subScroll,
                onCheckedChange = {
                    subScroll = it
                    prefs.subtitleScrollSeek.set(it)
                }
            )
        }
        item {
            var subDrag by remember { mutableStateOf(prefs.subtitleDrag.get()) }
            SwitchPreferenceItem(
                title = "سحب الترجمة لإعادة تحديد موضعها",
                subtitle = "المس الترجمة واسحبها لأعلى أو لأسفل لتحريكها. تبقى إيماءات السطوع والصوت فعّالة في بقية المناطق",
                checked = subDrag,
                onCheckedChange = {
                    subDrag = it
                    prefs.subtitleDrag.set(it)
                }
            )
        }
        item {
            var panZoom by remember { mutableStateOf(prefs.panAndZoom.get()) }
            SwitchPreferenceItem(
                title = "التحريك والتكبير",
                subtitle = "السماح بتحريك الفيديو (السحب) إلى جانب التكبير",
                checked = panZoom,
                onCheckedChange = {
                    panZoom = it
                    prefs.panAndZoom.set(it)
                }
            )
        }
        item {
            var prevent by remember { mutableStateOf(prefs.preventAccidentalSeek.get()) }
            SwitchPreferenceItem(
                title = "منع التقديم عند النقرات غير المقصودة",
                subtitle = "منع التقديم عند النقرات غير المقصودة",
                checked = prevent,
                onCheckedChange = {
                    prevent = it
                    prefs.preventAccidentalSeek.set(it)
                }
            )
        }

        // ═══ النقر المزدوج ═══
        item { SettingsSectionHeader("النقر المزدوج") }
        item {
            var ovals by remember { mutableStateOf(playerPrefs.showDoubleTapOvals.get()) }
            SwitchPreferenceItem(
                title = "إظهار موجة عند النقر المزدوج",
                subtitle = "إظهار موجة عند النقر المزدوج",
                checked = ovals,
                onCheckedChange = {
                    ovals = it
                    playerPrefs.showDoubleTapOvals.set(it)
                }
            )
        }
        item {
            var seekTime by remember { mutableStateOf(playerPrefs.showSeekTimeWhileSeeking.get()) }
            SwitchPreferenceItem(
                title = "إظهار وقت التقديم",
                subtitle = "إظهار وقت التقديم أثناء التقديم بالإيماءات",
                checked = seekTime,
                onCheckedChange = {
                    seekTime = it
                    playerPrefs.showSeekTimeWhileSeeking.set(it)
                }
            )
        }
    }
}
