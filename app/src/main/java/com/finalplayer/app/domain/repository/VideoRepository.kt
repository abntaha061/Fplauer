package com.finalplayer.app.domain.repository

import com.finalplayer.app.domain.model.VideoFolder
import com.finalplayer.app.domain.model.VideoItem
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getAllVideos(): Flow<List<VideoItem>>
    fun getVideosByFolder(folderPath: String): Flow<List<VideoItem>>
    fun getAllFolders(): Flow<List<VideoFolder>>
    suspend fun scanDeviceForVideos()
    suspend fun deleteVideo(videoId: String)
}
