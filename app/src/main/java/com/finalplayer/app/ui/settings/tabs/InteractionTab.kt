package com.finalplayer.app.ui.settings.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.finalplayer.app.ui.settings.SettingsViewModel
import com.finalplayer.app.ui.settings.components.PreferenceSectionHeader
import com.finalplayer.app.ui.settings.components.SwitchPreferenceItem

@Composable
fun InteractionTab(viewModel: SettingsViewModel) {
    val savePositionOnQuit by viewModel.savePositionOnQuit.collectAsState()
    val autoPiPOnNavigation by viewModel.autoPiPOnNavigation.collectAsState()
    val showSeekTimeWhileSeeking by viewModel.showSeekTimeWhileSeeking.collectAsState()
    val showDoubleTapOvals by viewModel.showDoubleTapOvals.collectAsState()
    val showLoadingCircle by viewModel.showLoadingCircle.collectAsState()

    var keepScreenOnWhenPaused by remember { mutableStateOf(true) }
    var preventAccidentalSeekClicks by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("interaction_tab_lazy_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item {
            PreferenceSectionHeader("سلوك التفاعل")

            SwitchPreferenceItem(
                title = "إبقاء الشاشة مضاءة عند الإيقاف المؤقت",
                subtitle = "منع إيقاف تشغيل الشاشة تلقائياً أثناء إيقاف الفيديو مؤقتاً",
                checked = keepScreenOnWhenPaused,
                onCheckedChange = { keepScreenOnWhenPaused = it }
            )

            SwitchPreferenceItem(
                title = "حفظ الموضع عند الخروج",
                subtitle = "الاستئناف التلقائي لآخر موضع شاهدته عند إعادة الفتح",
                checked = savePositionOnQuit,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.savePositionOnQuit, checked)
                }
            )

            SwitchPreferenceItem(
                title = "صورة في صورة تلقائياً",
                subtitle = "الانتقال لوضع PiP فور تصغير التطبيق أثناء التصفح",
                checked = autoPiPOnNavigation,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.autoPiPOnNavigation, checked)
                }
            )

            SwitchPreferenceItem(
                title = "إظهار شريط التقديم أثناء التقديم",
                subtitle = "عرض وقت التقديم المستهدف بصرياً أثناء التمرير على الشاشة",
                checked = showSeekTimeWhileSeeking,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.showSeekTimeWhileSeeking, checked)
                }
            )

            SwitchPreferenceItem(
                title = "إظهار موجة عند النقر المزدوج",
                subtitle = "عرض تأثير دوائر التقديم/التأخير المزدوجة على الأطراف",
                checked = showDoubleTapOvals,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.showDoubleTapOvals, checked)
                }
            )

            SwitchPreferenceItem(
                title = "إظهار مؤشر التحريك الدائري عند النقر المزدوج",
                subtitle = "عرض أيقونة تحميل أو مؤشر النقر المزدوج السريع",
                checked = showLoadingCircle,
                onCheckedChange = { checked ->
                    viewModel.setPreference(viewModel.playerPrefs.showLoadingCircle, checked)
                }
            )

            SwitchPreferenceItem(
                title = "منع التقديم عند النقرات غير المقصودة",
                subtitle = "تجاهل النقرات العشوائية السريعة القريبة من الحواف",
                checked = preventAccidentalSeekClicks,
                onCheckedChange = { preventAccidentalSeekClicks = it }
            )
        }
    }
}
