package com.finalplayer.app.domain.usecase

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.NetworkSourceType
import com.finalplayer.app.domain.model.StreamProxyConfig

class BuildStreamUrlUseCase {
    operator fun invoke(source: NetworkSource, path: String? = null, proxyConfig: StreamProxyConfig? = null): String {
        val scheme = when (source.type) {
            NetworkSourceType.SMB -> "smb"
            NetworkSourceType.FTP -> if (source.isSecure) "ftps" else "ftp"
            NetworkSourceType.WEBDAV -> if (source.isSecure) "https" else "http"
            NetworkSourceType.HTTP_DIRECT -> if (source.isSecure) "https" else "http"
            NetworkSourceType.M3U_PLAYLIST -> if (source.isSecure) "https" else "http"
        }

        val authPart = if (!source.username.isNullOrEmpty()) {
            if (!source.password.isNullOrEmpty()) {
                "${source.username}:${source.password}@"
            } else {
                "${source.username}@"
            }
        } else {
            ""
        }

        val portPart = if (source.port > 0) ":${source.port}" else ""
        val targetPath = path ?: source.sharePath ?: ""
        val normalizedPath = if (targetPath.isNotEmpty() && !targetPath.startsWith("/")) "/$targetPath" else targetPath

        val baseUrl = "$scheme://$authPart${source.host}$portPart$normalizedPath"

        return if (proxyConfig != null && proxyConfig.enabled && !proxyConfig.proxyHost.isNullOrEmpty() && proxyConfig.proxyPort != null) {
            val proxyTypeStr = proxyConfig.proxyType.name.lowercase()
            "$baseUrl#http-proxy=${proxyTypeStr}://${proxyConfig.proxyHost}:${proxyConfig.proxyPort}"
        } else {
            baseUrl
        }
    }
}
