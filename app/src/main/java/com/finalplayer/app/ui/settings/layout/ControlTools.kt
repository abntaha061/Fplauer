package com.finalplayer.app.ui.settings.layout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.ui.graphics.vector.ImageVector

data class ControlToolItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

object ControlTools {
    val ALL_TOOLS = listOf(
        ControlToolItem("chapters", "الفصول / الإشارات المرجعية", Icons.AutoMirrored.Filled.List),
        ControlToolItem("speed", "سرعة التشغيل", Icons.Default.Speed),
        ControlToolItem("decoder", "وحدة فك الترميز", Icons.Default.Memory),
        ControlToolItem("rotate", "تدوير الشاشة", Icons.Default.ScreenRotation),
        ControlToolItem("zoom", "تكبير الفيديو", Icons.Default.ZoomIn),
        ControlToolItem("frame_nav", "التنقل بين الإطارات", Icons.Default.FastForward),
        ControlToolItem("aspect_ratio", "نسبة العرض إلى الارتفاع", Icons.Default.AspectRatio),
        ControlToolItem("pip", "صورة داخل صورة", Icons.Default.PictureInPicture),
        ControlToolItem("lock", "قفل عناصر التحكم", Icons.Default.Lock),
        ControlToolItem("audio_track", "المسار الصوتي", Icons.Default.Audiotrack),
        ControlToolItem("more", "المزيد من الخيارات", Icons.Default.MoreVert),
        ControlToolItem("subtitles", "الترجمة", Icons.Default.Subtitles),
        ControlToolItem("current_chapter", "الفصل الحالي", Icons.Default.BookmarkBorder),
        ControlToolItem("repeat_mode", "وضع التكرار", Icons.Default.Repeat),
        ControlToolItem("flip_v", "قلب رأسي", Icons.Default.Flip),
        ControlToolItem("flip_h", "قلب أفقي", Icons.Default.FlipToBack),
        ControlToolItem("shuffle", "تشغيل عشوائي", Icons.Default.Shuffle),
        ControlToolItem("ab_repeat", "تكرار A-B", Icons.Default.RepeatOne),
        ControlToolItem("custom_skip", "تخطي مخصص", Icons.Default.Forward10),
        ControlToolItem("cinema", "الوضع السينمائي", Icons.Default.Movie),
        ControlToolItem("background_play", "التشغيل في الخلفية", Icons.Default.Headphones),
        ControlToolItem("sleep_timer", "مؤقت النوم", Icons.Default.Timer)
    )

    private val toolsMap = ALL_TOOLS.associateBy { it.id }

    fun getById(id: String): ControlToolItem? = toolsMap[id]
}
