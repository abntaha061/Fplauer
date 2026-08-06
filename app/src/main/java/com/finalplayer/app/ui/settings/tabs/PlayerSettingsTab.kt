package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel
import com.finalplayer.app.ui.settings.components.PreferenceItem
import com.finalplayer.app.ui.settings.components.PreferenceSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PlayerSettingsTab(viewModel: SettingsViewModel) {
    val defaultSpeed by viewModel.defaultSpeed.collectAsState()
    val savePositionOnQuit by viewModel.savePositionOnQuit.collectAsState()
    val autoPlayNext by viewModel.autoPlayNext.collectAsState()
    val autoPiPOnNavigation by viewModel.autoPiPOnNavigation.collectAsState()

    var selectedStatsPage by remember { mutableStateOf("إيقاف") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("player_settings_tab_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            PreferenceSectionHeader("التشغيل")

            // Default Speed with Slider
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سرعة التشغيل الافتراضية",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = String.format(Locale.US, "%.2fx", defaultSpeed),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = defaultSpeed.coerceIn(0.25f, 4.0f),
                        onValueChange = { rawVal ->
                            val rounded = (rawVal * 20).roundToInt() / 20f
                            viewModel.setPreference(viewModel.playerPrefs.defaultSpeed, rounded.coerceIn(0.25f, 4.0f))
                        },
                        valueRange = 0.25f..4.0f,
                        steps = 74,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SwitchPreferenceItem(
                title = "حفظ الموضع عند الخروج",
                subtitle = "الاستئناف التلقائي للموقع الأخير في الفيديو عند الفتح",
                checked = savePositionOnQuit,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.savePositionOnQuit, checked)
                }
            )

            SwitchPreferenceItem(
                title = "تشغيل الفيديو التالي تلقائياً",
                subtitle = "البدء بالفيديو الموالي مباشرة بعد انتهاء الفيديو الحالي",
                checked = autoPlayNext,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.autoPlayNext, checked)
                }
            )

            SwitchPreferenceItem(
                title = "صورة في صورة تلقائيًا عند الرجوع",
                subtitle = "الدخول في وضع PiP تلقائياً عند مغادرة التطبيق",
                checked = autoPiPOnNavigation,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.autoPiPOnNavigation, checked)
                }
            )
        }

        item {
            PreferenceSectionHeader("المزيد")

            PreferenceItem(
                title = "مؤقت النوم",
                subtitle = "ضبط مؤقت لإيقاف الفيديو تلقائياً",
                onClick = { /* Sleep timer picker */ }
            )

            // Default Stats Page Chips
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "صفحة الإحصائيات الافتراضية",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val statsPages = listOf("إيقاف", "1", "2", "3", "4", "5")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        statsPages.forEach { page ->
                            val isSelected = selectedStatsPage == page
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStatsPage = page },
                                label = {
                                    Text(
                                        text = page,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
