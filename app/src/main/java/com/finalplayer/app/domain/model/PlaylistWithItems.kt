package com.finalplayer.app.domain.model

data class PlaylistWithItems(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val items: List<VideoItem>,
    val totalDuration: Long,
    val coverThumbnail: String?
)
