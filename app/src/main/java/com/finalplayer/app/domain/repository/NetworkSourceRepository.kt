package com.finalplayer.app.domain.repository

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.VideoItem
import kotlinx.coroutines.flow.Flow

interface NetworkSourceRepository {
    fun getAllSources(): Flow<List<NetworkSource>>
    suspend fun addSource(source: NetworkSource)
    suspend fun removeSource(sourceId: String)
    suspend fun testConnection(source: NetworkSource): Result<Boolean>
    suspend fun browseRemoteFolder(source: NetworkSource, path: String): Result<List<VideoItem>>
}
