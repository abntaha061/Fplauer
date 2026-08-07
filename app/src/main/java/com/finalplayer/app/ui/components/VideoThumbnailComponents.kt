package com.finalplayer.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VideoThumbnailManager {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 8).coerceAtLeast(1024)
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    suspend fun getThumbnail(context: Context, videoUri: String): Bitmap? = withContext(Dispatchers.IO) {
        if (videoUri.isBlank()) return@withContext null

        memoryCache.get(videoUri)?.let { return@withContext it }

        val retriever = MediaMetadataRetriever()
        try {
            val uri = Uri.parse(videoUri)
            if (videoUri.startsWith("content://") || videoUri.startsWith("file://")) {
                retriever.setDataSource(context, uri)
            } else {
                retriever.setDataSource(videoUri)
            }

            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val durationUs = durationMs * 1000L

            // Target frame beyond initial black frames (e.g. 10s or 10% in)
            val timeUs = when {
                durationUs >= 15_000_000L -> 10_000_000L // 10s
                durationUs >= 5_000_000L -> 4_000_000L   // 4s
                durationUs > 0L -> durationUs / 2
                else -> 5_000_000L
            }

            var bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
                ?: retriever.frameAtTime

            // Check if frame is black or near-black
            if (bitmap != null && isMostlyBlack(bitmap)) {
                val altTimeUs = if (durationUs > 25_000_000L) 20_000_000L else (durationUs * 3 / 4)
                if (altTimeUs > 0) {
                    val altBitmap = retriever.getFrameAtTime(altTimeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (altBitmap != null && !isMostlyBlack(altBitmap)) {
                        bitmap = altBitmap
                    }
                }
            }

            if (bitmap != null) {
                memoryCache.put(videoUri, bitmap)
            }
            bitmap
        } catch (e: Exception) {
            null
        } finally {
            try {
                retriever.release()
            } catch (ignored: Exception) {}
        }
    }

    private fun isMostlyBlack(bitmap: Bitmap): Boolean {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) return true

        var totalLuminance = 0L
        var samples = 0
        val dx = (width / 6).coerceAtLeast(1)
        val dy = (height / 6).coerceAtLeast(1)

        for (i in 1..5) {
            for (j in 1..5) {
                val x = (i * dx).coerceIn(0, width - 1)
                val y = (j * dy).coerceIn(0, height - 1)
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val luminance = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                totalLuminance += luminance
                samples++
            }
        }
        val avgLuminance = if (samples > 0) totalLuminance / samples else 0
        return avgLuminance < 18
    }
}

@Composable
fun VideoThumbnailImage(
    videoUri: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    var bitmap by remember(videoUri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(videoUri) {
        bitmap = VideoThumbnailManager.getThumbnail(context, videoUri)
    }

    val currentBitmap = bitmap
    if (currentBitmap != null) {
        Image(
            bitmap = currentBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircleOutline,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun VideoStatusBadge(
    isOpened: Boolean,
    modifier: Modifier = Modifier
) {
    if (isOpened) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            shadowElevation = 2.dp
        ) {
            Text(
                text = "Running",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 2.dp
        ) {
            Text(
                text = "New",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
    }
}
