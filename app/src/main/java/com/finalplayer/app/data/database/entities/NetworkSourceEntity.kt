package com.finalplayer.app.data.database.entities

import android.util.Base64
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.NetworkSourceType

@Entity(tableName = "network_sources")
data class NetworkSourceEntity(
    @PrimaryKey val id: String,
    val type: String,
    val displayName: String,
    val host: String,
    val port: Int,
    val username: String?,
    val encryptedPassword: String?,
    val sharePath: String?,
    val isSecure: Boolean
) {
    fun toDomain(): NetworkSource {
        val decryptedPass = encryptedPassword?.let {
            try {
                String(Base64.decode(it, Base64.NO_WRAP), Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        }
        return NetworkSource(
            id = id,
            type = runCatching { NetworkSourceType.valueOf(type) }.getOrDefault(NetworkSourceType.SMB),
            displayName = displayName,
            host = host,
            port = port,
            username = username,
            password = decryptedPass,
            sharePath = sharePath,
            isSecure = isSecure
        )
    }

    companion object {
        fun fromDomain(domain: NetworkSource): NetworkSourceEntity {
            val encryptedPass = domain.password?.let {
                Base64.encodeToString(it.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            }
            return NetworkSourceEntity(
                id = domain.id,
                type = domain.type.name,
                displayName = domain.displayName,
                host = domain.host,
                port = domain.port,
                username = domain.username,
                encryptedPassword = encryptedPass,
                sharePath = domain.sharePath,
                isSecure = domain.isSecure
            )
        }
    }
}
