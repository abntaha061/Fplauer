package com.finalplayer.app

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
import com.finalplayer.app.ui.home.HomeScreen
import com.finalplayer.app.ui.home.HomeViewModel
import com.finalplayer.app.ui.onboarding.OnboardingScreen
import com.finalplayer.app.ui.onboarding.OnboardingViewModel
import com.finalplayer.app.ui.theme.FinalPlayerTheme
import org.koin.androidx.compose.koinViewModel

// TEST CODE - REMOVE LATER
// import com.finalplayer.app.player.core.MPVController
// import org.koin.android.ext.android.inject
// class MainActivity : ComponentActivity() {
//     private val mpvController: MPVController by inject()
//     fun testPlayVideo() {
//         mpvController.play("/storage/emulated/0/Movies/sample.mp4")
//     }
// }

class MainActivity : ComponentActivity() {
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
                                    // Folder click action
                                },
                                onPlayButtonClick = {
                                    // Play button action
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

