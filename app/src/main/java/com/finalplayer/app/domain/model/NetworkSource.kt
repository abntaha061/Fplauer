package com.finalplayer.app.domain.model

data class NetworkSource(
    val id: String,
    val type: NetworkSourceType,
    val displayName: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null,
    val sharePath: String? = null,
    val isSecure: Boolean = false
)
