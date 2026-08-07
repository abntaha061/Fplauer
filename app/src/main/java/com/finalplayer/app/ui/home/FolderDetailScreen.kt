package com.finalplayer.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.ui.components.VideoStatusBadge
import com.finalplayer.app.ui.components.VideoThumbnailImage
import com.finalplayer.app.ui.components.thinScrollbar
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderPath: String,
    viewModel: HomeViewModel = koinViewModel(),
    onVideoClick: (VideoItem) -> Unit,
    onBack: () -> Unit
) {
    val videos by viewModel.getVideosInFolder(folderPath)
        .collectAsState(initial = emptyList())
    val playedVideoIds by viewModel.playedVideoIds
        .collectAsState(initial = emptySet())

    val folderName = folderPath.substringAfterLast("/").ifEmpty { "الفولدر" }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = folderName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد فيديوهات في هذا الفولدر",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .thinScrollbar(state = listState, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                contentPadding = padding
            ) {
                items(videos, key = { it.id }) { video ->
                    VideoListItem(
                        video = video,
                        isOpened = playedVideoIds.contains(video.id),
                        onClick = { onVideoClick(video) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VideoListItem(
    video: VideoItem,
    isOpened: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Details Column (Title + Chips + Subtitle languages)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Resolution tag e.g. 1080p
                    val resText = when {
                        video.resolution != null && video.resolution.contains("x") -> {
                            val h = video.resolution.substringAfter("x").toIntOrNull() ?: 0
                            if (h > 0) "${h}p" else video.resolution
                        }
                        else -> "1080p"
                    }
                    MetaChip(text = resText)

                    // File size tag
                    val sizeText = formatFileSize(video.sizeBytes)
                    if (sizeText.isNotEmpty()) {
                        MetaChip(text = sizeText)
                    }

                    // Date tag
                    if (video.dateAdded > 0) {
                        MetaChip(text = formatDateShort(video.dateAdded))
                    }

                    // Subtitle Language Tags (e.g. ar, de, en)
                    val subLangs = listOf("ar", "de", "en")
                    subLangs.forEach { lang ->
                        SubtitleChip(language = lang)
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Thumbnail with Duration Overlay & Status Badge
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                VideoThumbnailImage(
                    videoUri = video.uri,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = video.title
                )

                // Duration Overlay (Bottom-Start)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    contentColor = Color.White
                ) {
                    Text(
                        text = formatDuration(video.duration),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                // New / Running Status Badge (Top-End)
                VideoStatusBadge(
                    isOpened = isOpened,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                )
            }
        }
    }
}

@Composable
private fun MetaChip(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SubtitleChip(language: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = language,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatDateShort(timestampSec: Long): String {
    val ms = if (timestampSec < 100000000000L) timestampSec * 1000L else timestampSec
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(ms))
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "00:00"
    val sec = ms / 1000
    val m = (sec % 3600) / 60
    val s = sec % 60
    val h = sec / 3600
    return if (h > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return ""
    val mb = bytes.toDouble() / (1024 * 1024)
    return if (mb >= 1024) {
        String.format(Locale.getDefault(), "%.2f GB", mb / 1024)
    } else {
        String.format(Locale.getDefault(), "%.1f MB", mb)
    }
}
