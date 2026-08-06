package com.finalplayer.app.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.finalplayer.app.data.database.dao.PlaylistDao
import com.finalplayer.app.data.database.dao.VideoDao
import com.finalplayer.app.data.database.entities.PlaylistEntity
import com.finalplayer.app.data.database.entities.PlaylistItemEntity
import com.finalplayer.app.data.database.entities.VideoEntity
import com.finalplayer.app.data.mapper.toDomainModel
import com.finalplayer.app.domain.model.PlaylistWithItems
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val context: Context,
    private val playlistDao: PlaylistDao,
    private val videoDao: VideoDao
) : PlaylistRepository {

    override val playlists: Flow<List<PlaylistWithItems>> = combine(
        playlistDao.getAllPlaylists(),
        videoDao.getAllVideos()
    ) { playlistEntities, allVideos ->
        val videoMap = allVideos.associateBy { it.id }
        playlistEntities.map { playlist ->
            val itemsEntities = playlistDao.getPlaylistItemsSync(playlist.id)
            val videoItems = itemsEntities.mapNotNull { itemEntity ->
                videoMap[itemEntity.videoId]?.toDomainModel()
            }
            val totalDuration = videoItems.sumOf { it.duration }
            val coverThumbnail = videoItems.firstOrNull { it.thumbnailPath != null }?.thumbnailPath

            PlaylistWithItems(
                id = playlist.id,
                name = playlist.name,
                createdAt = playlist.createdAt,
                items = videoItems,
                totalDuration = totalDuration,
                coverThumbnail = coverThumbnail
            )
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylistItems(playlistId)
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addVideoToPlaylist(playlistId: Long, videoId: String) = withContext(Dispatchers.IO) {
        val maxOrder = playlistDao.getMaxOrder(playlistId) ?: -1
        val newItem = PlaylistItemEntity(
            playlistId = playlistId,
            videoId = videoId,
            itemOrder = maxOrder + 1
        )
        playlistDao.insertPlaylistItem(newItem)
        Unit
    }

    override suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylistItem(playlistId, videoId)
    }

    override fun getPlaylistItems(playlistId: Long): Flow<List<VideoItem>> {
        return combine(
            playlistDao.getPlaylistItems(playlistId),
            videoDao.getAllVideos()
        ) { itemsEntities, allVideos ->
            val videoMap = allVideos.associateBy { it.id }
            itemsEntities.mapNotNull { itemEntity ->
                videoMap[itemEntity.videoId]?.toDomainModel()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun reorderItems(playlistId: Long, from: Int, to: Int) = withContext(Dispatchers.IO) {
        val currentItems = playlistDao.getPlaylistItemsSync(playlistId).toMutableList()
        if (from in currentItems.indices && to in currentItems.indices) {
            val movedItem = currentItems.removeAt(from)
            currentItems.add(to, movedItem)
            val reordered = currentItems.mapIndexed { index, item ->
                item.copy(itemOrder = index)
            }
            playlistDao.insertPlaylistItems(reordered)
        }
    }

    override suspend fun importFromM3U(uri: Uri): Result<PlaylistWithItems> = withContext(Dispatchers.IO) {
        try {
            val fileName = getFileName(uri) ?: "قائمة M3U"
            val playlistName = fileName.substringBeforeLast(".")
            val playlistId = playlistDao.insertPlaylist(PlaylistEntity(name = playlistName))

            val lines = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readLines() } ?: emptyList()
            var currentTitle = ""
            var order = 0
            val newVideos = mutableListOf<VideoEntity>()
            val playlistItems = mutableListOf<PlaylistItemEntity>()

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("#EXTINF:")) {
                    val commaIndex = trimmed.lastIndexOf(',')
                    if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                        currentTitle = trimmed.substring(commaIndex + 1).trim()
                    }
                } else if (trimmed.isNotBlank() && !trimmed.startsWith("#")) {
                    val itemTitle = if (currentTitle.isNotBlank()) currentTitle else trimmed.substringAfterLast('/')
                    val videoId = "m3u_${System.currentTimeMillis()}_${trimmed.hashCode()}"
                    
                    val videoEntity = VideoEntity(
                        id = videoId,
                        uri = trimmed,
                        title = itemTitle,
                        duration = 0L,
                        sizeBytes = 0L,
                        dateAdded = System.currentTimeMillis(),
                        folderPath = "M3U",
                        mimeType = "video/*"
                    )
                    newVideos.add(videoEntity)
                    playlistItems.add(
                        PlaylistItemEntity(
                            playlistId = playlistId,
                            videoId = videoId,
                            itemOrder = order++
                        )
                    )
                    currentTitle = ""
                }
            }

            if (newVideos.isNotEmpty()) {
                videoDao.insertVideos(newVideos)
                playlistDao.insertPlaylistItems(playlistItems)
            }

            val itemsDomain = newVideos.map { it.toDomainModel() }
            val playlistWithItems = PlaylistWithItems(
                id = playlistId,
                name = playlistName,
                createdAt = System.currentTimeMillis(),
                items = itemsDomain,
                totalDuration = 0L,
                coverThumbnail = null
            )
            Result.success(playlistWithItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) name = cursor.getString(index)
                }
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }
}
