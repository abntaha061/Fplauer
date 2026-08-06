package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.concurrent.TimeUnit

class SmbNetworkClient : NetworkClient {
    private var connection: Connection? = null
    private var session: Session? = null
    private var share: DiskShare? = null

    override suspend fun connect(source: NetworkSource): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val config = SmbConfig.builder()
                    .withTimeout(30, TimeUnit.SECONDS)
                    .build()
                val client = SMBClient(config)
                connection = client.connect(source.host, if (source.port > 0) source.port else 445)
                val authContext = if (!source.username.isNullOrEmpty()) {
                    AuthenticationContext(
                        source.username,
                        source.password?.toCharArray() ?: charArrayOf(),
                        null
                    )
                } else {
                    AuthenticationContext.anonymous()
                }
                session = connection!!.authenticate(authContext)
                val shareName = source.sharePath?.trim('/') ?: ""
                share = session!!.connectShare(shareName) as DiskShare
            }
        }
    }

    override suspend fun listFiles(path: String): Result<List<RemoteFile>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val currentShare = share ?: throw IllegalStateException("SMB Share not connected")
                val cleanPath = path.removePrefix("/").trim()
                currentShare.list(cleanPath).map { fileInfo ->
                    RemoteFile(
                        name = fileInfo.fileName,
                        path = if (cleanPath.isEmpty()) fileInfo.fileName else "$cleanPath/${fileInfo.fileName}",
                        isDirectory = (fileInfo.fileAttributes and FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) != 0L,
                        size = fileInfo.endOfFile,
                        lastModified = fileInfo.lastWriteTime.toEpochMillis()
                    )
                }.filter { it.name != "." && it.name != ".." }
            }
        }
    }

    override suspend fun openStream(path: String): Result<InputStream> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val currentShare = share ?: throw IllegalStateException("SMB Share not connected")
                val cleanPath = path.removePrefix("/").trim()
                val file = currentShare.openFile(
                    cleanPath,
                    setOf(AccessMask.GENERIC_READ),
                    null,
                    setOf(SMB2ShareAccess.FILE_SHARE_READ),
                    SMB2CreateDisposition.FILE_OPEN,
                    null
                )
                file.inputStream
            }
        }
    }

    override fun disconnect() {
        runCatching { share?.close() }
        runCatching { session?.close() }
        runCatching { connection?.close() }
        share = null
        session = null
        connection = null
    }
}
