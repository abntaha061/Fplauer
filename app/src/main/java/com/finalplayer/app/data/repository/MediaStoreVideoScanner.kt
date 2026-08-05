package com.finalplayer.app.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.finalplayer.app.data.database.entities.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaStoreVideoScanner(private val context: Context) {

    suspend fun scanDeviceVideos(): List<VideoEntity> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoEntity>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.MIME_TYPE
        )

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val dataColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val widthColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                val mimeTypeColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                while (c.moveToNext()) {
                    val id = c.getLong(idColumn)
                    val title = c.getString(nameColumn) ?: "Unknown Video"
                    val duration = c.getLong(durationColumn)
                    val sizeBytes = c.getLong(sizeColumn)
                    val dateAdded = c.getLong(dateAddedColumn)
                    val fullPath = c.getString(dataColumn) ?: ""
                    val width = c.getInt(widthColumn)
                    val height = c.getInt(heightColumn)
                    val mimeType = c.getString(mimeTypeColumn) ?: "video/*"

                    val folderPath = if (fullPath.isNotEmpty()) {
                        File(fullPath).parent ?: "/storage/emulated/0"
                    } else {
                        "/storage/emulated/0"
                    }

                    val resolution = if (width > 0 && height > 0) "${width}x${height}" else null
                    val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id).toString()

                    videoList.add(
                        VideoEntity(
                            id = id.toString(),
                            uri = uri,
                            title = title,
                            duration = duration,
                            sizeBytes = sizeBytes,
                            thumbnailPath = null,
                            dateAdded = dateAdded,
                            resolution = resolution,
                            folderPath = folderPath,
                            mimeType = mimeType
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        videoList
    }
}
