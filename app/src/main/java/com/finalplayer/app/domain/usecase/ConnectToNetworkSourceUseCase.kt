package com.finalplayer.app.domain.usecase

import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.domain.repository.NetworkSourceRepository

class ConnectToNetworkSourceUseCase(
    private val networkSourceRepository: NetworkSourceRepository
) {
    suspend operator fun invoke(source: NetworkSource, initialPath: String = "/"): Result<List<VideoItem>> {
        val testResult = networkSourceRepository.testConnection(source)
        if (testResult.isFailure) {
            return Result.failure(testResult.exceptionOrNull() ?: Exception("Connection test failed"))
        }
        return networkSourceRepository.browseRemoteFolder(source, initialPath)
    }
}
