package com.finalplayer.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.data.preferences.SortPreferences
import com.finalplayer.app.domain.repository.VideoRepository
import com.finalplayer.app.domain.usecase.GetVideoLibraryUseCase
import com.finalplayer.app.domain.usecase.ScanForVideosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideoLibraryUseCase: GetVideoLibraryUseCase,
    private val scanForVideosUseCase: ScanForVideosUseCase,
    private val videoRepository: VideoRepository,
    private val sortPreferences: SortPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var hasAutoScanned = false

    init {
        observeFoldersAndSort()
        refreshVideos()
    }

    private fun observeFoldersAndSort() {
        viewModelScope.launch {
            combine(
                videoRepository.getAllFolders(),
                sortPreferences.sortBy.flow,
                sortPreferences.sortAscending.flow,
                sortPreferences.viewMode.flow,
                sortPreferences.layoutMode.flow,
                sortPreferences.visibleFields.flow,
                sortPreferences.onlyForFolderList.flow
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                val folders = args[0] as? List<com.finalplayer.app.domain.model.VideoFolder> ?: emptyList()
                val sortBy = args[1] as? String ?: "title"
                val ascending = args[2] as? Boolean ?: true
                val viewMode = args[3] as? String ?: "folder"
                val layoutMode = args[4] as? String ?: "list"
                @Suppress("UNCHECKED_CAST")
                val fields = args[5] as? Set<String> ?: emptySet()
                val onlyFolderList = args[6] as? Boolean ?: false

                val sorted = when (sortBy) {
                    "date" -> folders.sortedBy { it.path }
                    "size" -> folders.sortedBy { it.videoCount }
                    else -> folders.sortedBy { it.name.lowercase() }
                }
                val finalFolders = if (ascending) sorted else sorted.reversed()

                _uiState.value.copy(
                    folders = finalFolders,
                    sortBy = sortBy,
                    sortAscending = ascending,
                    viewMode = viewMode,
                    layoutMode = layoutMode,
                    visibleFields = fields,
                    onlyForFolderList = onlyFolderList
                )
            }.collect { newState ->
                _uiState.value = newState
                if (newState.folders.isEmpty() && !_uiState.value.isLoading && !hasAutoScanned) {
                    hasAutoScanned = true
                    refreshVideos()
                }
            }
        }
    }

    fun setSortBy(sortBy: String) {
        viewModelScope.launch { sortPreferences.sortBy.set(sortBy) }
    }

    fun setSortAscending(ascending: Boolean) {
        viewModelScope.launch { sortPreferences.sortAscending.set(ascending) }
    }

    fun setViewMode(viewMode: String) {
        viewModelScope.launch { sortPreferences.viewMode.set(viewMode) }
    }

    fun setLayoutMode(layoutMode: String) {
        viewModelScope.launch { sortPreferences.layoutMode.set(layoutMode) }
    }

    fun setVisibleFields(fields: Set<String>) {
        viewModelScope.launch { sortPreferences.visibleFields.set(fields) }
    }

    fun setOnlyForFolderList(only: Boolean) {
        viewModelScope.launch { sortPreferences.onlyForFolderList.set(only) }
    }

    fun refreshVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                scanForVideosUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}

