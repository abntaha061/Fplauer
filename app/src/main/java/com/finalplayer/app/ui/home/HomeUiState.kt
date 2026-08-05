package com.finalplayer.app.ui.home

import com.finalplayer.app.domain.model.VideoFolder

data class HomeUiState(
    val folders: List<VideoFolder> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: HomeTab = HomeTab.HOME
)

enum class HomeTab {
    PLAYLISTS,
    RECENTS,
    HOME
}
