package com.finalplayer.app.data.repository

import com.finalplayer.app.data.database.dao.VideoDao
import com.finalplayer.app.data.mapper.toDomainModel
import com.finalplayer.app.data.mapper.toVideoFolders
import com.finalplayer.app.domain.model.VideoFolder
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoRepositoryImpl(
    private val videoDao: VideoDao,
    private val mediaStoreScanner: MediaStoreVideoScanner
) : VideoRepository {

    override fun getAllVideos(): Flow<List<VideoItem>> {
        return videoDao.getAllVideos().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getVideosByFolder(folderPath: String): Flow<List<VideoItem>> {
        return videoDao.getVideosByFolder(folderPath).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllFolders(): Flow<List<VideoFolder>> {
        return videoDao.getAllVideos().map { entities ->
            entities.toVideoFolders()
        }
    }

    override suspend fun scanDeviceForVideos() {
        val scannedVideos = mediaStoreScanner.scanDeviceVideos()
        if (scannedVideos.isNotEmpty()) {
            videoDao.insertVideos(scannedVideos)
        }
    }

    override suspend fun deleteVideo(videoId: String) {
        videoDao.deleteVideo(videoId)
    }
}
