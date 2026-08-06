package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpDirectClient : NetworkClient {
    private var currentSource: NetworkSource? = null

    override suspend fun connect(source: NetworkSource): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                currentSource = source
                val scheme = if (source.isSecure) "https" else "http"
                val portStr = if (source.port > 0) ":${source.port}" else ""
                val pathStr = source.sharePath ?: ""
                val urlString = "$scheme://${source.host}$portStr$pathStr"

                val connection = URL(urlString).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.requestMethod = "HEAD"
                val responseCode = connection.responseCode
                connection.disconnect()

                if (responseCode !in 200..399) {
                    throw IllegalStateException("HTTP connection failed with code: $responseCode")
                }
            }
        }
    }

    override suspend fun listFiles(path: String): Result<List<RemoteFile>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val source = currentSource ?: throw IllegalStateException("HTTP Client not connected")
                val fileName = source.displayName.ifEmpty { "Stream" }
                val scheme = if (source.isSecure) "https" else "http"
                val portStr = if (source.port > 0) ":${source.port}" else ""
                val fullUrl = if (path.startsWith("http://") || path.startsWith("https://")) {
                    path
                } else {
                    "$scheme://${source.host}$portStr$path"
                }

                listOf(
                    RemoteFile(
                        name = fileName,
                        path = fullUrl,
                        isDirectory = false,
                        size = 0L,
                        lastModified = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    override suspend fun openStream(path: String): Result<InputStream> {
        return withContext(Dispatchers.IO) {
            runCatching {
                URL(path).openStream()
            }
        }
    }

    override fun disconnect() {
        currentSource = null
    }
}
