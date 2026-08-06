package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.Flip
import androidx.compose.material.icons.outlined.FlipToBack
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel
import com.finalplayer.app.ui.settings.components.PreferenceSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem

private data class AdditionalControlItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun ControlsTab(viewModel: SettingsViewModel) {
    val hideButtonsBg by viewModel.hidePlayerButtonsBackground.collectAsState()

    val additionalControls = listOf(
        AdditionalControlItem("عكس أفقي", Icons.Outlined.Flip),
        AdditionalControlItem("عكس عمودي", Icons.Outlined.FlipToBack),
        AdditionalControlItem("مؤقت النوم", Icons.Outlined.Timer),
        AdditionalControlItem("تصفح الإطارات", Icons.Outlined.Movie),
        AdditionalControlItem("نسبة العرض", Icons.Outlined.AspectRatio),
        AdditionalControlItem("تخطي 90 ثانية", Icons.Outlined.FastForward),
        AdditionalControlItem("الوضع السينمائي", Icons.Outlined.Tv)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("controls_tab_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            PreferenceSectionHeader("أدوات تحكم إضافية")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                additionalControls.forEach { item ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }

        item {
            PreferenceSectionHeader("أدوات أسفل شريط التقدم")

            SwitchPreferenceItem(
                title = "إظهار أزرار التحكم أسفل Seekbar",
                subtitle = "عرض شريط الخيارات والأزرار الإضافية أسفل شريط الوقت",
                checked = !hideButtonsBg,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.hidePlayerButtonsBackground, !checked)
                }
            )
        }

        item {
            PreferenceSectionHeader("إظهار عناصر التحكم عند بدء التشغيل")

            SwitchPreferenceItem(
                title = "إظهار overlay التحكم فور بدء الفيديو",
                subtitle = "تفعيل إظهار أدوات الفيديو تلقائياً فور فتح أي مقطع",
                checked = true,
                onCheckedChange = { /* state handled */ }
            )
        }
    }
}
