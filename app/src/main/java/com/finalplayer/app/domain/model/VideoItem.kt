package com.finalplayer.app.domain.model

data class VideoItem(
    val id: String,
    val uri: String,
    val title: String,
    val duration: Long,
    val sizeBytes: Long,
    val thumbnailPath: String? = null,
    val dateAdded: Long,
    val resolution: String? = null,
    val folderPath: String
)
