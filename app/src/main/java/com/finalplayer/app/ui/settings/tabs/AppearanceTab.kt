package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel
import com.finalplayer.app.ui.settings.components.PreferenceSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem

@Composable
fun AppearanceTab(viewModel: SettingsViewModel) {
    val hidePlayerButtonsBackground by viewModel.hidePlayerButtonsBackground.collectAsState()
    val glassmorphismControls by viewModel.glassmorphismControls.collectAsState()
    val glassmorphismSeekbar by viewModel.glassmorphismSeekbar.collectAsState()
    val useSpringAnimations by viewModel.useSpringAnimations.collectAsState()
    val matchControlsToTheme by viewModel.matchControlsToTheme.collectAsState()
    val playerAlwaysDark by viewModel.playerAlwaysDark.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("appearance_tab_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            PreferenceSectionHeader("المظهر المخصص")

            SwitchPreferenceItem(
                title = "إخفاء خلفية أزرار المشغل",
                subtitle = "جعل أزرار التحكم شفافة بدون خلفيات داكنة",
                checked = hidePlayerButtonsBackground,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.hidePlayerButtonsBackground, checked)
                }
            )

            SwitchPreferenceItem(
                title = "عناصر تحكم المشغل الزجاجية (Glassmorphic)",
                subtitle = "إضافة تأثير التضبيب والشفافية الزجاجية على أزرار التحكم",
                checked = glassmorphismControls,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.glassmorphismControls, checked)
                }
            )

            SwitchPreferenceItem(
                title = "خلفية شريط التحريك الزجاجية",
                subtitle = "تطبيق تأثير الزجاج المضبب على شريط Seekbar",
                checked = glassmorphismSeekbar,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.glassmorphismSeekbar, checked)
                }
            )

            SwitchPreferenceItem(
                title = "تفعيل الحركات المرنة (Spring animations)",
                subtitle = "استخدام تأثيرات مرنة عند فتح وإغلاق القوائم وعناصر التحكم",
                checked = useSpringAnimations,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.useSpringAnimations, checked)
                }
            )

            SwitchPreferenceItem(
                title = "مطابقة عناصر التحكم للسمة",
                subtitle = "تعديل ألوان التحكم لتناسب سمة النظام الحالية",
                checked = matchControlsToTheme,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.matchControlsToTheme, checked)
                }
            )

            SwitchPreferenceItem(
                title = "المشغّل دائمًا في الوضع الداكن",
                subtitle = "الحفاظ على الثيم الداكن لمشغل الفيديو بصرف النظر عن سمة النظام",
                checked = playerAlwaysDark,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.appearancePrefs.playerAlwaysDark, checked)
                }
            )
        }
    }
}
