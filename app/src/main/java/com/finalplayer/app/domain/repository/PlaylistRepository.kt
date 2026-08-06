package com.finalplayer.app.domain.repository

import android.net.Uri
import com.finalplayer.app.domain.model.PlaylistWithItems
import com.finalplayer.app.domain.model.VideoItem
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    val playlists: Flow<List<PlaylistWithItems>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addVideoToPlaylist(playlistId: Long, videoId: String)
    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String)
    fun getPlaylistItems(playlistId: Long): Flow<List<VideoItem>>
    suspend fun reorderItems(playlistId: Long, from: Int, to: Int)
    suspend fun importFromM3U(uri: Uri): Result<PlaylistWithItems>
}
