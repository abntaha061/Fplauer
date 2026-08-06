package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.IOException
import java.io.InputStream

class FtpNetworkClient : NetworkClient {
    private val ftpClient = FTPClient()

    override suspend fun connect(source: NetworkSource): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                ftpClient.connectTimeout = 15000
                ftpClient.connect(source.host, if (source.port > 0) source.port else 21)
                val loginSuccess = if (!source.username.isNullOrEmpty()) {
                    ftpClient.login(source.username, source.password ?: "")
                } else {
                    ftpClient.login("anonymous", "anonymous@")
                }
                if (!loginSuccess) throw IOException("FTP login failed")
                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                Unit
            }
        }
    }

    override suspend fun listFiles(path: String): Result<List<RemoteFile>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val files = ftpClient.listFiles(path) ?: emptyArray()
                files.map { file ->
                    RemoteFile(
                        name = file.name,
                        path = if (path.endsWith("/")) "$path${file.name}" else "$path/${file.name}",
                        isDirectory = file.isDirectory,
                        size = file.size,
                        lastModified = file.timestamp?.timeInMillis ?: System.currentTimeMillis()
                    )
                }.filter { it.name != "." && it.name != ".." }
            }
        }
    }

    override suspend fun openStream(path: String): Result<InputStream> {
        return withContext(Dispatchers.IO) {
            runCatching {
                ftpClient.retrieveFileStream(path) ?: throw IOException("Cannot open FTP stream")
            }
        }
    }

    override fun disconnect() {
        runCatching {
            if (ftpClient.isConnected) {
                ftpClient.disconnect()
            }
        }
    }
}
