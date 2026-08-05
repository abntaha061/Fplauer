package com.finalplayer.app.data.network.client

import com.finalplayer.app.domain.model.NetworkSourceType

class NetworkClientFactory(
    private val smbClient: SmbNetworkClient,
    private val ftpClient: FtpNetworkClient,
    private val httpClient: HttpDirectClient
) {
    fun getClient(type: NetworkSourceType): NetworkClient {
        return when (type) {
            NetworkSourceType.SMB -> smbClient
            NetworkSourceType.FTP -> ftpClient
            NetworkSourceType.WEBDAV,
            NetworkSourceType.HTTP_DIRECT,
            NetworkSourceType.M3U_PLAYLIST -> httpClient
        }
    }
}
