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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel
import com.finalplayer.app.ui.settings.components.PreferenceSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem

@Composable
fun GesturesTab(viewModel: SettingsViewModel) {
    val swapVolumeAndBrightness by viewModel.swapVolumeAndBrightness.collectAsState()

    var selectedOrientation by remember { mutableStateOf("الفيديو") }
    var brightnessGestures by remember { mutableStateOf(true) }
    var volumeGestures by remember { mutableStateOf(true) }
    var pinchToZoom by remember { mutableStateOf(true) }
    var horizontalSeekSwipe by remember { mutableStateOf(true) }
    var subtitleSeekSwipe by remember { mutableStateOf(true) }
    var dragSubPosition by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gestures_tab_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            PreferenceSectionHeader("الاتجاه")

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
                        text = "اتجاه الشاشة عند التشغيل",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val orientations = listOf("الفيديو", "تلقائي", "أفقي", "عمودي")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        orientations.forEach { orientation ->
                            FilterChip(
                                selected = selectedOrientation == orientation,
                                onClick = { selectedOrientation = orientation },
                                label = {
                                    Text(
                                        text = orientation,
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

        item {
            PreferenceSectionHeader("الإيماءات")

            SwitchPreferenceItem(
                title = "إيماءات السطوع",
                subtitle = "السحب العمودي في الجانب الأيسر للتحكم بالسطوع",
                checked = brightnessGestures,
                onCheckedChange = { brightnessGestures = it }
            )

            SwitchPreferenceItem(
                title = "إيماءات الصوت",
                subtitle = "السحب العمودي في الجانب الأيمن للتحكم بالصوت",
                checked = volumeGestures,
                onCheckedChange = { volumeGestures = it }
            )

            SwitchPreferenceItem(
                title = "تبديل أماكن إيماءات الصوت والسطوع",
                subtitle = "عكس الجوانب المخصصة للسطوع والصوت",
                checked = swapVolumeAndBrightness,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.swapVolumeAndBrightness, checked)
                }
            )

            SwitchPreferenceItem(
                title = "قرص للتكبير (Pinch to zoom)",
                subtitle = "استخدام إيماءة الأصابع للتكبير والتصغير",
                checked = pinchToZoom,
                onCheckedChange = { pinchToZoom = it }
            )

            SwitchPreferenceItem(
                title = "التمرير الأفقي للتقديم",
                subtitle = "السحب الأفقي لتقديم أو تأخير الفيديو",
                checked = horizontalSeekSwipe,
                onCheckedChange = { horizontalSeekSwipe = it }
            )

            SwitchPreferenceItem(
                title = "التمرير للتقديم في الترجمة",
                subtitle = "التنقل بين جمل الترجمة عن طريق التمرير الأفقي",
                checked = subtitleSeekSwipe,
                onCheckedChange = { subtitleSeekSwipe = it }
            )

            SwitchPreferenceItem(
                title = "سحب الترجمة لإعادة تحديد موضعها",
                subtitle = "تأشير وسحب الترجمة مباشرة على الشاشة",
                checked = dragSubPosition,
                onCheckedChange = { dragSubPosition = it }
            )
        }
    }
}
