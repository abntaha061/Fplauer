package com.finalplayer.app.domain.usecase

import android.net.Uri
import com.finalplayer.app.data.network.proxy.ProxyManager
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.NetworkSourceType

class BuildStreamUrlUseCase(
    private val proxyManager: ProxyManager? = null
) {
    operator fun invoke(source: NetworkSource, filePath: String): String {
        return when (source.type) {
            NetworkSourceType.SMB -> {
                val share = source.sharePath?.trim('/') ?: ""
                val cleanPath = if (filePath.startsWith("/")) filePath else "/$filePath"
                val sharePart = if (share.isNotEmpty()) "/$share" else ""
                "smb://${buildAuth(source)}${source.host}${sharePart}$cleanPath"
            }
            NetworkSourceType.FTP -> {
                val portStr = if (source.port > 0) ":${source.port}" else ""
                val cleanPath = if (filePath.startsWith("/")) filePath else "/$filePath"
                "ftp://${buildAuth(source)}${source.host}$portStr$cleanPath"
            }
            NetworkSourceType.HTTP_DIRECT, NetworkSourceType.WEBDAV, NetworkSourceType.M3U_PLAYLIST -> filePath
        }
    }

    private fun buildAuth(source: NetworkSource): String {
        if (source.username.isNullOrEmpty()) return ""
        val encodedPass = Uri.encode(source.password ?: "")
        return "${Uri.encode(source.username)}:$encodedPass@"
    }
}
