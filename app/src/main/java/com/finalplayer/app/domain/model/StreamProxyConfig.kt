package com.finalplayer.app.domain.model

data class StreamProxyConfig(
    val enabled: Boolean = false,
    val proxyHost: String? = null,
    val proxyPort: Int? = null,
    val proxyType: ProxyType = ProxyType.NONE,
    val requiresAuth: Boolean = false
)
