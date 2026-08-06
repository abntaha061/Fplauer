package com.finalplayer.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.finalplayer.app.player.PlayerActivity
import com.finalplayer.app.ui.about.AboutScreen
import com.finalplayer.app.ui.about.LibrariesScreen
import com.finalplayer.app.ui.browser.NetworkBrowserScreen
import com.finalplayer.app.ui.home.HomeScreen
import com.finalplayer.app.ui.home.HomeViewModel
import com.finalplayer.app.ui.onboarding.OnboardingScreen
import com.finalplayer.app.ui.onboarding.OnboardingViewModel
import com.finalplayer.app.ui.search.SearchScreen
import com.finalplayer.app.ui.settings.SettingsScreen
import com.finalplayer.app.ui.theme.FinalPlayerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private fun openPlayer(path: String, title: String) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_VIDEO_PATH, path)
            putExtra(PlayerActivity.EXTRA_VIDEO_TITLE, title)
        }
        startActivity(intent)
    }

    private fun openPlaylistPlayer(playlistId: Long, index: Int = 0) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_PLAYLIST_ID, playlistId)
            putExtra(PlayerActivity.EXTRA_PLAYLIST_INDEX, index)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val onboardingViewModel: OnboardingViewModel = koinViewModel()
                    val homeViewModel: HomeViewModel = koinViewModel()

                    val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsState()
                    val navController = rememberNavController()

                    val startDestination = if (hasCompletedOnboarding) "home" else "onboarding"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                viewModel = onboardingViewModel,
                                onPermissionGranted = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onFolderClick = { folderPath ->
                                    openPlayer(folderPath, "Folder Video")
                                },
                                onPlaylistClick = { playlist ->
                                    openPlaylistPlayer(playlist.id)
                                },
                                onRecentVideoClick = { path, title ->
                                    openPlayer(path, title)
                                },
                                onPlayButtonClick = {
                                    val firstFolder = homeViewModel.uiState.value.folders.firstOrNull()
                                    if (firstFolder != null) {
                                        openPlayer(firstFolder.path, firstFolder.name)
                                    } else {
                                        openPlayer("", "Demo Video")
                                    }
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                },
                                onNetworkSourcesClick = {
                                    navController.navigate("network_browser")
                                },
                                onSearchClick = {
                                    navController.navigate("search")
                                }
                            )
                        }

                        composable("search") {
                            SearchScreen(
                                onBack = { navController.popBackStack() },
                                onVideoClick = { videoId, title ->
                                    openPlayer(videoId, title)
                                },
                                onFolderClick = { folderPath ->
                                    openPlayer(folderPath, "Folder Video")
                                }
                            )
                        }

                        composable("network_browser") {
                            NetworkBrowserScreen(
                                onBack = { navController.popBackStack() },
                                onPlayMedia = { streamUrl, title ->
                                    openPlayer(streamUrl, title)
                                }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onAboutClick = { navController.navigate("about") }
                            )
                        }

                        composable("about") {
                            AboutScreen(
                                onBack = { navController.popBackStack() },
                                onOpenLibraries = { navController.navigate("libraries") }
                            )
                        }

                        composable("libraries") {
                            LibrariesScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
