package com.finalplayer.app.data.repository

import com.finalplayer.app.data.database.dao.NetworkSourceDao
import com.finalplayer.app.data.database.entities.NetworkSourceEntity
import com.finalplayer.app.data.network.client.NetworkClientFactory
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import com.finalplayer.app.domain.repository.NetworkSourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkSourceRepositoryImpl(
    private val networkSourceDao: NetworkSourceDao,
    private val clientFactory: NetworkClientFactory
) : NetworkSourceRepository {

    override fun getAllSources(): Flow<List<NetworkSource>> {
        return networkSourceDao.getAllSources().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addSource(source: NetworkSource) {
        networkSourceDao.insertSource(NetworkSourceEntity.fromDomain(source))
    }

    override suspend fun removeSource(sourceId: String) {
        networkSourceDao.deleteSource(sourceId)
    }

    override suspend fun testConnection(source: NetworkSource): Result<Boolean> {
        val client = clientFactory.getClient(source.type)
        return client.connect(source).map {
            client.disconnect()
            true
        }
    }

    override suspend fun browseRemoteFolder(
        source: NetworkSource,
        path: String
    ): Result<List<RemoteFile>> {
        val client = clientFactory.getClient(source.type)
        return client.connect(source).fold(
            onSuccess = {
                val listResult = client.listFiles(path)
                client.disconnect()
                listResult
            },
            onFailure = { error ->
                client.disconnect()
                Result.failure(error)
            }
        )
    }
}
