package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import java.io.InputStream

interface NetworkClient {
    suspend fun connect(source: NetworkSource): Result<Unit>
    suspend fun listFiles(path: String): Result<List<RemoteFile>>
    suspend fun openStream(path: String): Result<InputStream>
    fun disconnect()
}
