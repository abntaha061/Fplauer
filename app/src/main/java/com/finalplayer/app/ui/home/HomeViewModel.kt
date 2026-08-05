package com.finalplayer.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.domain.repository.VideoRepository
import com.finalplayer.app.domain.usecase.GetVideoLibraryUseCase
import com.finalplayer.app.domain.usecase.ScanForVideosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVideoLibraryUseCase: GetVideoLibraryUseCase,
    private val scanForVideosUseCase: ScanForVideosUseCase,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            folders = emptyList(),
            isLoading = false,
            selectedTab = HomeTab.HOME
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeFolders()
    }

    private fun observeFolders() {
        viewModelScope.launch {
            videoRepository.getAllFolders().collect { folders ->
                _uiState.update { it.copy(folders = folders) }
                if (folders.isEmpty() && !_uiState.value.isLoading) {
                    refreshVideos()
                }
            }
        }
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

