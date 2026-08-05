package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.VideoItem

interface NetworkClient {
    suspend fun connect(source: NetworkSource): Result<Boolean>
    suspend fun listFiles(source: NetworkSource, path: String): Result<List<VideoItem>>
    suspend fun openStream(source: NetworkSource, path: String): Result<String>
}
