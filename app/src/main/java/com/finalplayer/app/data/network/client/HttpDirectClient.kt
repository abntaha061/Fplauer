package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.VideoItem

class HttpDirectClient : NetworkClient {
    override suspend fun connect(source: NetworkSource): Result<Boolean> {
        // TODO: Validate HTTP / WebDAV / M3U direct endpoint accessibility
        return Result.failure(NotImplementedError("HTTP direct client connection not implemented yet"))
    }

    override suspend fun listFiles(source: NetworkSource, path: String): Result<List<VideoItem>> {
        // TODO: Parse M3U playlist or WebDAV directory index
        return Result.failure(NotImplementedError("HTTP file listing not implemented yet"))
    }

    override suspend fun openStream(source: NetworkSource, path: String): Result<String> {
        // TODO: Return direct stream URL
        return Result.failure(NotImplementedError("HTTP stream opening not implemented yet"))
    }
}
