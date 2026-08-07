package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import com.finalplayer.app.player.core.MPVLib
import com.finalplayer.app.ui.settings.components.NavigationPreferenceItem
import com.finalplayer.app.ui.settings.components.SettingsSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem
import org.koin.compose.koinInject

@Composable
fun PlayerSettingsTab() {
    val prefs: PlayerPreferences = koinInject()
    val subPrefs: SubtitlesPreferences = koinInject()

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // ═══ المظهر ═══
        item { SettingsSectionHeader("المظهر") }
        item {
            NavigationPreferenceItem(
                title = "المظهر",
                subtitle = "الوضع الداكن، Material You",
                icon = Icons.Default.Palette,
                onClick = { /* navigate to theme */ }
            )
        }

        // ═══ التشغيل ═══
        item { SettingsSectionHeader("التشغيل") }
        item {
            NavigationPreferenceItem(
                title = "المشغّل",
                subtitle = "الاتجاه والإيماءات وعناصر التحكم",
                icon = Icons.Default.PlayArrow,
                onClick = { /* navigate */ }
            )
        }
        item {
            NavigationPreferenceItem(
                title = "وحدة فك الترميز",
                subtitle = "فك ترميز عتادي، تنسيق البكسل، إزالة التدرجات",
                icon = Icons.Default.Memory,
                onClick = { /* navigate */ }
            )
        }
        item {
            NavigationPreferenceItem(
                title = "الصوت",
                subtitle = "اللغات المفضلة وقنوات الصوت وتصحيح النغمة",
                icon = Icons.Default.MusicNote,
                onClick = { /* navigate */ }
            )
        }

        // ═══ الإيماءات وعناصر التحكم ═══
        item { SettingsSectionHeader("الإيماءات وعناصر التحكم") }
        item {
            NavigationPreferenceItem(
                title = "الإيماءات",
                subtitle = "نقر مزدوج، عناصر تحكم الوسائط",
                icon = Icons.Default.Gesture,
                onClick = { /* navigate */ }
            )
        }
        item {
            NavigationPreferenceItem(
                title = "تخطيط المشغّل",
                subtitle = "ترتيب الأزرار ومواضعها",
                icon = Icons.Default.ViewModule,
                onClick = { /* navigate */ }
            )
        }

        // ═══ الترجمة ═══
        item { SettingsSectionHeader("الترجمة") }
        item {
            var autoEnable by remember {
                mutableStateOf(subPrefs.autoEnableSubtitles.get())
            }
            SwitchPreferenceItem(
                title = "تحميل الترجمة تلقائياً",
                subtitle = "تحميل ملفات الترجمة الخارجية ذات الاسم نفسه تلقائياً",
                checked = autoEnable,
                onCheckedChange = {
                    autoEnable = it
                    subPrefs.autoEnableSubtitles.set(it)
                }
            )
        }
        item {
            var forceOverride by remember {
                mutableStateOf(subPrefs.overrideAssSubs.get())
            }
            SwitchPreferenceItem(
                title = "تجاوز ترجمة ASS/SSA",
                subtitle = "فرض تجاوز تنسيق ترجمة ASS/SSA",
                checked = forceOverride,
                onCheckedChange = {
                    forceOverride = it
                    subPrefs.overrideAssSubs.set(it)
                    // تطبيق فوري على MPV
                    MPVLib.setOptionString(
                        "sub-ass-override",
                        if (it) "force" else "scale"
                    )
                }
            )
        }
        item {
            var scaleByWindow by remember {
                mutableStateOf(subPrefs.scaleByWindow.get())
            }
            SwitchPreferenceItem(
                title = "التحجيم حسب النافذة",
                subtitle = "تحجيم الترجمة حسب حجم النافذة واستخدام هوامش الفيديو",
                checked = scaleByWindow,
                onCheckedChange = {
                    scaleByWindow = it
                    subPrefs.scaleByWindow.set(it)
                    MPVLib.setOptionString("sub-scale-by-window", if (it) "yes" else "no")
                    MPVLib.setOptionString("sub-use-margins", if (it) "yes" else "no")
                }
            )
        }
    }
}
