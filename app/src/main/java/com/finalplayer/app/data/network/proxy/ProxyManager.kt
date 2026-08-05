package com.finalplayer.app.data.network.proxy

import com.finalplayer.app.domain.model.ProxyType
import com.finalplayer.app.domain.model.StreamProxyConfig
import java.net.InetSocketAddress
import java.net.Proxy

class ProxyManager {

    fun getProxySettings(config: StreamProxyConfig): Proxy? {
        if (!config.enabled || config.proxyHost.isNullOrEmpty() || config.proxyPort == null) {
            return Proxy.NO_PROXY
        }

        val type = when (config.proxyType) {
            ProxyType.HTTP -> Proxy.Type.HTTP
            ProxyType.SOCKS4, ProxyType.SOCKS5 -> Proxy.Type.SOCKS
            ProxyType.NONE -> Proxy.Type.DIRECT
        }

        if (type == Proxy.Type.DIRECT) {
            return Proxy.NO_PROXY
        }

        val address = InetSocketAddress(config.proxyHost, config.proxyPort)
        return Proxy(type, address)
    }

    fun buildProxiedUrl(originalUrl: String, config: StreamProxyConfig): String {
        if (!config.enabled || config.proxyHost.isNullOrEmpty() || config.proxyPort == null || config.proxyType == ProxyType.NONE) {
            return originalUrl
        }

        val proxyScheme = when (config.proxyType) {
            ProxyType.HTTP -> "http"
            ProxyType.SOCKS4 -> "socks4"
            ProxyType.SOCKS5 -> "socks5"
            ProxyType.NONE -> return originalUrl
        }

        val proxyUrlParam = "$proxyScheme://${config.proxyHost}:${config.proxyPort}"
        return if (originalUrl.contains("#")) {
            "$originalUrl;http-proxy=$proxyUrlParam"
        } else {
            "$originalUrl#http-proxy=$proxyUrlParam"
        }
    }
}
