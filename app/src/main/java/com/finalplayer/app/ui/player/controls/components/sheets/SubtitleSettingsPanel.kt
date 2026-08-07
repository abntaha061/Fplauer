package com.finalplayer.app.ui.player.controls.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalplayer.app.data.preferences.SubtitlesPreferences
import com.finalplayer.app.player.core.MPVLib
import com.finalplayer.app.ui.components.SidePanel
import com.finalplayer.app.ui.components.thinScrollbar
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * الخيارات المتاحة لاستهداف لون الترجمة
 */
enum class ColorTarget(val title: String) {
    TEXT("النص"),
    BORDER("الحدود"),
    BACKGROUND("صندوق معتم")
}

/**
 * حاوية لوحة إعدادات الترجمات (SubtitleSettingsPanel)
 * توفر إمكانية تخصيص الخطوط والطباعة والألوان للترجمات مع التحديث المباشر لمشغل MPV
 */
@Composable
fun SubtitleSettingsPanel(
    onDismiss: () -> Unit,
    subPrefs: SubtitlesPreferences = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    // الحالة المحلية للطباعة والخط
    var isBold by remember { mutableStateOf(subPrefs.bold.get()) }
    var isItalic by remember { mutableStateOf(subPrefs.italic.get()) }
    var fontSize by remember { mutableIntStateOf(subPrefs.fontSize.get()) }
    var borderSize by remember { mutableIntStateOf(subPrefs.borderSize.get().toInt()) }
    var shadowOffset by remember { mutableIntStateOf(subPrefs.shadowOffset.get()) }

    // الحالة المحلية للألوان (A, R, G, B)
    var selectedColorTarget by remember { mutableStateOf(ColorTarget.TEXT) }

    val textColorLong = subPrefs.textColor.get()
    val borderColorLong = subPrefs.borderColor.get()
    val backgroundColorLong = subPrefs.backgroundColor.get()

    // فك ترميز الألوان إلى ARGB
    var textAlpha by remember { mutableIntStateOf(((textColorLong shr 24) and 0xFF).toInt()) }
    var textRed by remember { mutableIntStateOf(((textColorLong shr 16) and 0xFF).toInt()) }
    var textGreen by remember { mutableIntStateOf(((textColorLong shr 8) and 0xFF).toInt()) }
    var textBlue by remember { mutableIntStateOf((textColorLong and 0xFF).toInt()) }

    var borderAlpha by remember { mutableIntStateOf(((borderColorLong shr 24) and 0xFF).toInt()) }
    var borderRed by remember { mutableIntStateOf(((borderColorLong shr 16) and 0xFF).toInt()) }
    var borderGreen by remember { mutableIntStateOf(((borderColorLong shr 8) and 0xFF).toInt()) }
    var borderBlue by remember { mutableIntStateOf((borderColorLong and 0xFF).toInt()) }

    var bgAlpha by remember { mutableIntStateOf(((backgroundColorLong shr 24) and 0xFF).toInt()) }
    var bgRed by remember { mutableIntStateOf(((backgroundColorLong shr 16) and 0xFF).toInt()) }
    var bgGreen by remember { mutableIntStateOf(((backgroundColorLong shr 8) and 0xFF).toInt()) }
    var bgBlue by remember { mutableIntStateOf((backgroundColorLong and 0xFF).toInt()) }

    // تهيئة الخصائص في مشغل MPV عند فتح اللوحة لأول مرة
    LaunchedEffect(Unit) {
        applyTypographyToMPV(isBold, isItalic, fontSize, borderSize, shadowOffset)
        applyColorToMPV(ColorTarget.TEXT, textAlpha, textRed, textGreen, textBlue)
        applyColorToMPV(ColorTarget.BORDER, borderAlpha, borderRed, borderGreen, borderBlue)
        applyColorToMPV(ColorTarget.BACKGROUND, bgAlpha, bgRed, bgGreen, bgBlue)
    }

    SidePanel(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("subtitle_settings_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // الهيدر الرئيسي: العنوان وزر الإغلاق
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إعدادات الترجمات",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_subtitle_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .thinScrollbar(listState)
            ) {
                // كارت الطباعة والخط Typography
                item {
                    SubtitleSettingsTypographyCard(
                        isBold = isBold,
                        onBoldChange = { boldVal ->
                            isBold = boldVal
                            // sub-bold: تفعيل الخط العريض
                            MPVLib.setPropertyBoolean("sub-bold", boldVal)
                            MPVLib.setPropertyInt("sub-bold", if (boldVal) 1 else 0)
                            MPVLib.setOptionString("sub-bold", if (boldVal) "yes" else "no")
                            coroutineScope.launch { subPrefs.bold.set(boldVal) }
                        },
                        isItalic = isItalic,
                        onItalicChange = { italicVal ->
                            isItalic = italicVal
                            // sub-italic: تفعيل الخط المائل
                            MPVLib.setPropertyBoolean("sub-italic", italicVal)
                            MPVLib.setPropertyInt("sub-italic", if (italicVal) 1 else 0)
                            MPVLib.setOptionString("sub-italic", if (italicVal) "yes" else "no")
                            coroutineScope.launch { subPrefs.italic.set(italicVal) }
                        },
                        fontSize = fontSize,
                        onFontSizeChange = { size ->
                            fontSize = size
                            // sub-font-size: حجم الخط لملفات الترجمة (1 إلى 100)
                            MPVLib.setPropertyInt("sub-font-size", size)
                            MPVLib.setOptionString("sub-font-size", size.toString())
                            coroutineScope.launch { subPrefs.fontSize.set(size) }
                        },
                        borderSize = borderSize,
                        onBorderSizeChange = { border ->
                            borderSize = border
                            // sub-outline-size: حجم الحدود الخارجية (0 إلى 20)
                            MPVLib.setPropertyInt("sub-outline-size", border)
                            MPVLib.setPropertyFloat("sub-border-size", border.toFloat())
                            MPVLib.setOptionString("sub-outline-size", border.toString())
                            coroutineScope.launch { subPrefs.borderSize.set(border.toFloat()) }
                        },
                        shadowOffset = shadowOffset,
                        onShadowOffsetChange = { offset ->
                            shadowOffset = offset
                            // sub-shadow-offset: إزاحة الظل للترجمة (0 إلى 100)
                            MPVLib.setPropertyInt("sub-shadow-offset", offset)
                            MPVLib.setPropertyFloat("sub-shadow-offset", offset.toFloat())
                            MPVLib.setOptionString("sub-shadow-offset", offset.toString())
                            coroutineScope.launch { subPrefs.shadowOffset.set(offset) }
                        }
                    )
                }

                // كارت الألوان Colors
                item {
                    SubtitleSettingsColorsCard(
                        selectedTarget = selectedColorTarget,
                        onTargetSelected = { target -> selectedColorTarget = target },
                        textAlpha = textAlpha, textRed = textRed, textGreen = textGreen, textBlue = textBlue,
                        borderAlpha = borderAlpha, borderRed = borderRed, borderGreen = borderGreen, borderBlue = borderBlue,
                        bgAlpha = bgAlpha, bgRed = bgRed, bgGreen = bgGreen, bgBlue = bgBlue,
                        onColorChanged = { target, a, r, g, b ->
                            when (target) {
                                ColorTarget.TEXT -> {
                                    textAlpha = a; textRed = r; textGreen = g; textBlue = b
                                    val longVal = packARGB(a, r, g, b)
                                    coroutineScope.launch { subPrefs.textColor.set(longVal) }
                                }
                                ColorTarget.BORDER -> {
                                    borderAlpha = a; borderRed = r; borderGreen = g; borderBlue = b
                                    val longVal = packARGB(a, r, g, b)
                                    coroutineScope.launch { subPrefs.borderColor.set(longVal) }
                                }
                                ColorTarget.BACKGROUND -> {
                                    bgAlpha = a; bgRed = r; bgGreen = g; bgBlue = b
                                    val longVal = packARGB(a, r, g, b)
                                    coroutineScope.launch { subPrefs.backgroundColor.set(longVal) }
                                }
                            }
                            // تطبيق التغيير فوراً على مشغل MPV
                            applyColorToMPV(target, a, r, g, b)
                        }
                    )
                }
            }
        }
    }
}

/**
 * كارت ضبط خصائص الطباعة والخط للترجمات (Typography Card)
 */
@Composable
fun SubtitleSettingsTypographyCard(
    isBold: Boolean,
    onBoldChange: (Boolean) -> Unit,
    isItalic: Boolean,
    onItalicChange: (Boolean) -> Unit,
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,
    borderSize: Int,
    onBorderSizeChange: (Int) -> Unit,
    shadowOffset: Int,
    onShadowOffsetChange: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("typography_card"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "الطباعة والخط (Typography)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // خيارات النمط: عريض ومائل
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // sub-bold: تفعيل الخط العريض
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatBold,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "عريض", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isBold,
                        onCheckedChange = onBoldChange,
                        modifier = Modifier.testTag("bold_switch")
                    )
                }

                // sub-italic: تفعيل الخط المائل
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatItalic,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "مائل", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isItalic,
                        onCheckedChange = onItalicChange,
                        modifier = Modifier.testTag("italic_switch")
                    )
                }
            }

            // sub-font-size: حجم الخط (1 إلى 100)
            LabeledSlider(
                title = "حجم الخط",
                value = fontSize,
                valueRange = 1f..100f,
                onValueChange = { onFontSizeChange(it) },
                testTag = "font_size_slider"
            )

            // sub-outline-size: حجم الحدود الخارجية (0 إلى 20)
            LabeledSlider(
                title = "حجم الحدود",
                value = borderSize,
                valueRange = 0f..20f,
                onValueChange = { onBorderSizeChange(it) },
                testTag = "border_size_slider"
            )

            // sub-shadow-offset: إزاحة الظل (0 إلى 100)
            LabeledSlider(
                title = "إزاحة الظل",
                value = shadowOffset,
                valueRange = 0f..100f,
                onValueChange = { onShadowOffsetChange(it) },
                testTag = "shadow_offset_slider"
            )
        }
    }
}

/**
 * كارت ألوان الترجمات (Colors Card)
 * يتيح اختيار الهدف (النص، الحدود، خلفية معتمة) وضبط ألوان ARGB مع المعاينة المباشرة
 */
@Composable
fun SubtitleSettingsColorsCard(
    selectedTarget: ColorTarget,
    onTargetSelected: (ColorTarget) -> Unit,
    textAlpha: Int, textRed: Int, textGreen: Int, textBlue: Int,
    borderAlpha: Int, borderRed: Int, borderGreen: Int, borderBlue: Int,
    bgAlpha: Int, bgRed: Int, bgGreen: Int, bgBlue: Int,
    onColorChanged: (target: ColorTarget, a: Int, r: Int, g: Int, b: Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("colors_card"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "الألوان (Colors)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.FormatColorFill,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // محدد العنصر المستهدف للتلوين (النص، الحدود، الصندوق)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorTarget.values().forEach { target ->
                    val isSelected = selectedTarget == target
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTargetSelected(target) },
                        label = { Text(target.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // صندوق معاينة الترجمة المباشرة Live Preview Box
            val textColor = Color(textRed, textGreen, textBlue, textAlpha)
            val borderColor = Color(borderRed, borderGreen, borderBlue, borderAlpha)
            val bgColor = Color(bgRed, bgGreen, bgBlue, bgAlpha)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "معاينة النص - Subtitle Preview",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // استخراج القيم الحالية بناءً على محدد اللون الحالي
            val currentA = when (selectedTarget) {
                ColorTarget.TEXT -> textAlpha
                ColorTarget.BORDER -> borderAlpha
                ColorTarget.BACKGROUND -> bgAlpha
            }
            val currentR = when (selectedTarget) {
                ColorTarget.TEXT -> textRed
                ColorTarget.BORDER -> borderRed
                ColorTarget.BACKGROUND -> bgRed
            }
            val currentG = when (selectedTarget) {
                ColorTarget.TEXT -> textGreen
                ColorTarget.BORDER -> borderGreen
                ColorTarget.BACKGROUND -> bgGreen
            }
            val currentB = when (selectedTarget) {
                ColorTarget.TEXT -> textBlue
                ColorTarget.BORDER -> borderBlue
                ColorTarget.BACKGROUND -> bgBlue
            }

            // عرض كود الهيكس الحالي #AARRGGBB
            val currentHex = String.format("#%02X%02X%02X%02X", currentA, currentR, currentG, currentB)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ترميز الهيكس:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentHex,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 4 سلايدرات ARGB
            LabeledSlider(
                title = "الشفافية (Alpha)",
                value = currentA,
                valueRange = 0f..255f,
                onValueChange = { onColorChanged(selectedTarget, it, currentR, currentG, currentB) },
                testTag = "color_alpha_slider"
            )

            LabeledSlider(
                title = "الأحمر (Red)",
                value = currentR,
                valueRange = 0f..255f,
                onValueChange = { onColorChanged(selectedTarget, currentA, it, currentG, currentB) },
                testTag = "color_red_slider"
            )

            LabeledSlider(
                title = "الأخضر (Green)",
                value = currentG,
                valueRange = 0f..255f,
                onValueChange = { onColorChanged(selectedTarget, currentA, currentR, it, currentB) },
                testTag = "color_green_slider"
            )

            LabeledSlider(
                title = "الأزرق (Blue)",
                value = currentB,
                valueRange = 0f..255f,
                onValueChange = { onColorChanged(selectedTarget, currentA, currentR, currentG, it) },
                testTag = "color_blue_slider"
            )
        }
    }
}

/**
 component slider مع عنوان وقيمة نصية رقمية
 */
@Composable
fun LabeledSlider(
    title: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    testTag: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

/**
 * دالة مساعدة لتطبيق إعدادات الخط والطباعة في MPV
 */
private fun applyTypographyToMPV(
    bold: Boolean,
    italic: Boolean,
    fontSize: Int,
    borderSize: Int,
    shadowOffset: Int
) {
    // sub-bold: تفعيل أو إلغاء الخط العريض
    MPVLib.setPropertyBoolean("sub-bold", bold)
    MPVLib.setPropertyInt("sub-bold", if (bold) 1 else 0)
    MPVLib.setOptionString("sub-bold", if (bold) "yes" else "no")

    // sub-italic: تفعيل أو إلغاء الخط المائل
    MPVLib.setPropertyBoolean("sub-italic", italic)
    MPVLib.setPropertyInt("sub-italic", if (italic) 1 else 0)
    MPVLib.setOptionString("sub-italic", if (italic) "yes" else "no")

    // sub-font-size: حجم الخط (1..100)
    MPVLib.setPropertyInt("sub-font-size", fontSize)
    MPVLib.setOptionString("sub-font-size", fontSize.toString())

    // sub-outline-size: حجم الحدود (0..20)
    MPVLib.setPropertyInt("sub-outline-size", borderSize)
    MPVLib.setPropertyFloat("sub-border-size", borderSize.toFloat())
    MPVLib.setOptionString("sub-outline-size", borderSize.toString())

    // sub-shadow-offset: إزاحة الظل (0..100)
    MPVLib.setPropertyInt("sub-shadow-offset", shadowOffset)
    MPVLib.setPropertyFloat("sub-shadow-offset", shadowOffset.toFloat())
    MPVLib.setOptionString("sub-shadow-offset", shadowOffset.toString())
}

/**
 * دالة مساعدة لتحويل وقيم ARGB وتطبيق اللون المحدد في MPV بترميز الهيكس (#AARRGGBB)
 */
private fun applyColorToMPV(target: ColorTarget, a: Int, r: Int, g: Int, b: Int) {
    // sub-color: لون النص الرئيسي للترجمة بترميز الهيكس (#AARRGGBB)
    // sub-border-color: لون الحدود الخارجية للترجمة بترميز الهيكس (#AARRGGBB)
    // sub-back-color: لون خلفية الترجمة الصندوقية بترميز الهيكس (#AARRGGBB)
    val hexColor = String.format("#%02X%02X%02X%02X", a, r, g, b)

    val property = when (target) {
        ColorTarget.TEXT -> "sub-color"
        ColorTarget.BORDER -> "sub-border-color"
        ColorTarget.BACKGROUND -> "sub-back-color"
    }

    MPVLib.setPropertyString(property, hexColor)
    MPVLib.setOptionString(property, hexColor)
}

/**
 * دالة مساعدة لتغليف أرقام ARGB إلى Long لقيم الحفظ
 */
private fun packARGB(a: Int, r: Int, g: Int, b: Int): Long {
    return ((a.toLong() and 0xFF) shl 24) or
            ((r.toLong() and 0xFF) shl 16) or
            ((g.toLong() and 0xFF) shl 8) or
            (b.toLong() and 0xFF)
}
