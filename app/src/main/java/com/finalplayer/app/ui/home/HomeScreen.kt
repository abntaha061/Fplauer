package com.finalplayer.app.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.finalplayer.app.domain.model.PlaylistWithItems
import com.finalplayer.app.ui.home.components.FolderCard
import com.finalplayer.app.ui.home.components.HomeBottomBar
import com.finalplayer.app.ui.home.components.HomeTopBar
import com.finalplayer.app.ui.home.components.SortBottomSheet
import com.finalplayer.app.ui.playlists.PlaylistsScreen
import com.finalplayer.app.ui.recents.RecentsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onFolderClick: (String) -> Unit = {},
    onPlaylistClick: (PlaylistWithItems) -> Unit = {},
    onRecentVideoClick: (String, String) -> Unit = { _, _ -> },
    onPlayButtonClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNetworkSourcesClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showSortSheet by remember { mutableStateOf(false) }
    val sortSheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.refreshVideos()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshVideos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            if (uiState.selectedTab == HomeTab.HOME) {
                HomeTopBar(
                    onSettingsClick = onSettingsClick,
                    onNetworkSourcesClick = onNetworkSourcesClick,
                    onSortClick = { showSortSheet = true },
                    onSearchClick = onSearchClick,
                    onRefreshClick = { viewModel.refreshVideos() }
                )
            }
        },
        bottomBar = {
            HomeBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.selectTab(tab)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                HomeTab.PLAYLISTS -> {
                    PlaylistsScreen(
                        onPlaylistClick = onPlaylistClick
                    )
                }
                HomeTab.RECENTS -> {
                    RecentsScreen(
                        onVideoClick = onRecentVideoClick
                    )
                }
                HomeTab.HOME -> {
                    if (uiState.isLoading && uiState.folders.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (!uiState.isLoading && uiState.folders.isEmpty()) {
                        Text(
                            text = "لم يتم العثور على فيديوهات / No videos found",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                        ) {
                            items(
                                items = uiState.folders,
                                key = { folder -> folder.path }
                            ) { folder ->
                                FolderCard(
                                    folder = folder,
                                    onClick = { onFolderClick(folder.path) }
                                )
                            }
                        }
                    }

                    // Circular Floating Action Button in bottom-left corner
                    FloatingActionButton(
                        onClick = onPlayButtonClick,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp, bottom = 20.dp),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play"
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        SortBottomSheet(
            sheetState = sortSheetState,
            sortBy = uiState.sortBy,
            sortAscending = uiState.sortAscending,
            viewMode = uiState.viewMode,
            layoutMode = uiState.layoutMode,
            visibleFields = uiState.visibleFields,
            onlyForFolderList = uiState.onlyForFolderList,
            onDismiss = { showSortSheet = false },
            onSortByChanged = { viewModel.setSortBy(it) },
            onSortAscendingChanged = { viewModel.setSortAscending(it) },
            onViewModeChanged = { viewModel.setViewMode(it) },
            onLayoutModeChanged = { viewModel.setLayoutMode(it) },
            onVisibleFieldsChanged = { viewModel.setVisibleFields(it) },
            onOnlyForFolderListChanged = { viewModel.setOnlyForFolderList(it) }
        )
    }
}

