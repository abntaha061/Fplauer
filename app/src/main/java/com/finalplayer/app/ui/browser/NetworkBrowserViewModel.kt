package com.finalplayer.app.ui.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.domain.model.NetworkSource
import com.finalplayer.app.domain.model.RemoteFile
import com.finalplayer.app.domain.repository.NetworkSourceRepository
import com.finalplayer.app.domain.usecase.BuildStreamUrlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetworkBrowserViewModel(
    private val repository: NetworkSourceRepository,
    private val buildStreamUrlUseCase: BuildStreamUrlUseCase
) : ViewModel() {

    val sources: StateFlow<List<NetworkSource>> = repository.getAllSources()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _connectedSource = MutableStateFlow<NetworkSource?>(null)
    val connectedSource: StateFlow<NetworkSource?> = _connectedSource.asStateFlow()

    private val _currentPath = MutableStateFlow("/")
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val _pathSegments = MutableStateFlow<List<String>>(emptyList())
    val pathSegments: StateFlow<List<String>> = _pathSegments.asStateFlow()

    private val _remoteFiles = MutableStateFlow<List<RemoteFile>>(emptyList())
    val remoteFiles: StateFlow<List<RemoteFile>> = _remoteFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting.asStateFlow()

    private val _testResult = MutableStateFlow<Result<Boolean>?>(null)
    val testResult: StateFlow<Result<Boolean>?> = _testResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun testConnection(source: NetworkSource) {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null
            val result = repository.testConnection(source)
            _testResult.value = result
            _isTesting.value = false
        }
    }

    fun addSource(source: NetworkSource) {
        viewModelScope.launch {
            repository.addSource(source)
        }
    }

    fun removeSource(sourceId: String) {
        viewModelScope.launch {
            if (_connectedSource.value?.id == sourceId) {
                disconnect()
            }
            repository.removeSource(sourceId)
        }
    }

    fun connectAndBrowse(source: NetworkSource, path: String = "/") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _connectedSource.value = source
            _currentPath.value = path
            updatePathSegments(path)

            val result = repository.browseRemoteFolder(source, path)
            result.fold(
                onSuccess = { files ->
                    _remoteFiles.value = files
                },
                onFailure = { error ->
                    _errorMessage.value = error.localizedMessage ?: "Failed to connect to network source"
                    _remoteFiles.value = emptyList()
                }
            )
            _isLoading.value = false
        }
    }

    fun browseDirectory(folderPath: String) {
        val source = _connectedSource.value ?: return
        connectAndBrowse(source, folderPath)
    }

    fun navigateToBreadcrumb(index: Int) {
        val segments = _pathSegments.value
        if (index < 0 || index >= segments.size) return
        val targetPath = if (index == 0) "/" else "/" + segments.subList(1, index + 1).joinToString("/")
        browseDirectory(targetPath)
    }

    fun navigateBack(): Boolean {
        if (_connectedSource.value == null) return false
        val segments = _pathSegments.value
        if (segments.size <= 1) {
            disconnect()
            return true
        } else {
            navigateToBreadcrumb(segments.size - 2)
            return true
        }
    }

    fun disconnect() {
        _connectedSource.value = null
        _currentPath.value = "/"
        _pathSegments.value = emptyList()
        _remoteFiles.value = emptyList()
        _errorMessage.value = null
    }

    fun buildStreamUrl(file: RemoteFile): String {
        val source = _connectedSource.value ?: return file.path
        return buildStreamUrlUseCase(source, file.path)
    }

    fun clearTestResult() {
        _testResult.value = null
    }

    private fun updatePathSegments(path: String) {
        val clean = path.trim('/')
        if (clean.isEmpty()) {
            _pathSegments.value = listOf("Root")
        } else {
            _pathSegments.value = listOf("Root") + clean.split('/')
        }
    }
}
