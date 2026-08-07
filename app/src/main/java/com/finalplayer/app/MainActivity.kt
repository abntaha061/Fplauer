package com.finalplayer.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finalplayer.app.player.PlayerActivity
import com.finalplayer.app.ui.about.AboutScreen
import com.finalplayer.app.ui.about.LibrariesScreen
import com.finalplayer.app.ui.browser.NetworkBrowserScreen
import com.finalplayer.app.ui.home.FolderDetailScreen
import com.finalplayer.app.ui.home.HomeScreen
import com.finalplayer.app.ui.home.HomeViewModel
import com.finalplayer.app.ui.onboarding.OnboardingScreen
import com.finalplayer.app.ui.onboarding.OnboardingViewModel
import com.finalplayer.app.ui.search.SearchScreen
import com.finalplayer.app.ui.settings.SettingsScreen
import com.finalplayer.app.ui.theme.FinalPlayerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private fun encodeNavPath(path: String): String {
        if (path.isEmpty()) return ""
        return path.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }
    }

    private fun decodeNavPath(encoded: String): String {
        if (encoded.isEmpty()) return ""
        return try {
            val clean = encoded.trim()
            val isHex = clean.length % 2 == 0 && clean.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
            if (isHex) {
                val bytes = ByteArray(clean.length / 2)
                for (i in clean.indices step 2) {
                    bytes[i / 2] = clean.substring(i, i + 2).toInt(16).toByte()
                }
                String(bytes, Charsets.UTF_8)
            } else {
                try {
                    String(Base64.decode(clean, Base64.NO_WRAP or Base64.URL_SAFE), Charsets.UTF_8)
                } catch (_: Exception) {
                    Uri.decode(clean)
                }
            }
        } catch (e: Exception) {
            Uri.decode(encoded)
        }
    }

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

                    val hasCompletedOnboardingState by onboardingViewModel.hasCompletedOnboarding.collectAsState()

                    if (hasCompletedOnboardingState == null) {
                        return@Surface
                    }

                    val hasCompletedOnboarding = hasCompletedOnboardingState == true
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
                                    val encoded = encodeNavPath(folderPath)
                                    navController.navigate("folder_detail/$encoded")
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
                                        val encoded = encodeNavPath(firstFolder.path)
                                        navController.navigate("folder_detail/$encoded")
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

                        composable(
                            route = "folder_detail/{folderPath}",
                            arguments = listOf(navArgument("folderPath") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rawArg = backStackEntry.arguments?.getString("folderPath") ?: ""
                            val folderPath = decodeNavPath(rawArg)
                            FolderDetailScreen(
                                folderPath = folderPath,
                                viewModel = homeViewModel,
                                onVideoClick = { video ->
                                    openPlayer(video.uri, video.title)
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("search") {
                            SearchScreen(
                                onBack = { navController.popBackStack() },
                                onVideoClick = { videoId, title ->
                                    openPlayer(videoId, title)
                                },
                                onFolderClick = { folderPath ->
                                    val encoded = encodeNavPath(folderPath)
                                    navController.navigate("folder_detail/$encoded")
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

                        composable(
                            route = "settings?sub={sub}",
                            arguments = listOf(navArgument("sub") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val sub = backStackEntry.arguments?.getString("sub")
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                initialSubScreen = sub
                            )
                        }

                        composable("about") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                initialSubScreen = "about"
                            )
                        }

                        composable("libraries") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                initialSubScreen = "libraries"
                            )
                        }
                    }
                }
            }
        }
    }
}
