package com.finalplayer.app.ui.home

import com.finalplayer.app.domain.model.VideoFolder

data class HomeUiState(
    val folders: List<VideoFolder> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: HomeTab = HomeTab.HOME,
    val sortBy: String = "title",
    val sortAscending: Boolean = true,
    val viewMode: String = "folder",
    val layoutMode: String = "list",
    val visibleFields: Set<String> = setOf("Path", "Folder Size", "Total Media"),
    val onlyForFolderList: Boolean = false
)

enum class HomeTab {
    PLAYLISTS,
    RECENTS,
    HOME
}
