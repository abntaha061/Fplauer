package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.VideoItem

class FtpNetworkClient : NetworkClient {
    override suspend fun connect(source: NetworkSource): Result<Boolean> {
        // TODO: Implement FTP/FTPS connection using commons-net library
        return Result.failure(NotImplementedError("FTP client connection not implemented yet"))
    }

    override suspend fun listFiles(source: NetworkSource, path: String): Result<List<VideoItem>> {
        // TODO: Implement FTP directory listing using commons-net library
        return Result.failure(NotImplementedError("FTP file listing not implemented yet"))
    }

    override suspend fun openStream(source: NetworkSource, path: String): Result<String> {
        // TODO: Build FTP stream URI for player
        return Result.failure(NotImplementedError("FTP stream opening not implemented yet"))
    }
}
