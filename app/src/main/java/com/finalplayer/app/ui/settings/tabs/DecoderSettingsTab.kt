package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalplayer.app.data.preferences.DecoderPreferences
import com.finalplayer.app.data.preferences.PlayerPreferences
import com.finalplayer.app.player.core.MPVLib
import com.finalplayer.app.ui.settings.components.DropdownPreferenceItem
import com.finalplayer.app.ui.settings.components.SettingsSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DecoderSettingsTab() {
    val prefs: DecoderPreferences = koinInject()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // ═══ فك الترميز ═══
        item { SettingsSectionHeader("فك الترميز") }
        item {
            var hwDec by remember { mutableStateOf(prefs.tryHWDecoding.get()) }
            SwitchPreferenceItem(
                title = "تفعيل فك الترميز العتادي",
                subtitle = "استخدام GPU لفك الترميز (أسرع وأقل استهلاكاً)",
                checked = hwDec,
                onCheckedChange = {
                    hwDec = it
                    prefs.tryHWDecoding.set(it)
                    MPVLib.setPropertyString("hwdec",
                        if (it) "mediacodec,mediacodec-copy,no" else "no")
                }
            )
        }
        item {
            var gpuNext by remember { mutableStateOf(prefs.gpuNext.get()) }
            SwitchPreferenceItem(
                title = "استخدام gpu-next",
                subtitle = "محرك رسوميات أحدث — يوفر جودة أعلى مع Vulkan",
                checked = gpuNext,
                onCheckedChange = {
                    gpuNext = it
                    prefs.gpuNext.set(it)
                    // يتطلب إعادة تشغيل MPV
                }
            )
        }
        item {
            var yuv by remember { mutableStateOf(prefs.useYUV420P.get()) }
            SwitchPreferenceItem(
                title = "فرض تنسيق YUV420P",
                subtitle = "تحويل قسري لـ YUV420P — يحل مشاكل الألوان على بعض الأجهزة",
                checked = yuv,
                onCheckedChange = {
                    yuv = it
                    prefs.useYUV420P.set(it)
                    MPVLib.setOptionString("vf",
                        if (it) "format=yuv420p" else "")
                }
            )
        }

        // ═══ الجودة البصرية ═══
        item { SettingsSectionHeader("الجودة البصرية") }
        item {
            val options = listOf("None", "CPU", "GPU")
            var debanding by remember { mutableStateOf(prefs.debanding.get()) }
            DropdownPreferenceItem(
                title = "إزالة التدرجات (Debanding)",
                subtitle = debanding,
                options = options,
                selectedOption = debanding,
                onOptionSelected = {
                    debanding = it
                    prefs.debanding.set(it)
                    when (it) {
                        "CPU" -> MPVLib.command(
                            "vf", "add", "@deband:gradfun=radius=12")
                        "GPU" -> MPVLib.setOptionString("deband", "yes")
                        else -> {
                            MPVLib.command("vf", "remove", "@deband")
                            MPVLib.setOptionString("deband", "no")
                        }
                    }
                }
            )
        }

        // ═══ إحصائيات ═══
        item { SettingsSectionHeader("إحصائيات") }
        item {
            val playerPrefs: PlayerPreferences = koinInject()
            var statsPage by remember { mutableIntStateOf(playerPrefs.enabledStatisticsPage.get()) }
            // chips أفقية
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("صفحة الإحصائيات الافتراضية",
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = statsPage == 0,
                        onClick = {
                            statsPage = 0
                            playerPrefs.enabledStatisticsPage.set(0)
                        },
                        label = { Text("إيقاف") }
                    )
                    (1..5).forEach { page ->
                        FilterChip(
                            selected = statsPage == page,
                            onClick = {
                                statsPage = page
                                playerPrefs.enabledStatisticsPage.set(page)
                                MPVLib.command("script-binding", "stats/display-stats-toggle")
                                MPVLib.command("script-binding", "stats/display-page-$page")
                            },
                            label = { Text("الصفحة $page") }
                        )
                    }
                }
            }
        }
    }
}
