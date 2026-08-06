package com.finalplayer.app.player

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.finalplayer.app.domain.repository.PlaylistRepository
import com.finalplayer.app.player.core.MpvTeardownCoordinator
import com.finalplayer.app.ui.player.PlayerScreen
import com.finalplayer.app.ui.theme.FinalPlayerTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : ComponentActivity() {

    private val viewModel: PlayerViewModel by viewModel()
    private val playlistRepository: PlaylistRepository by inject()

    private var videoId: String = ""
    private var videoPath: String = ""
    private var videoTitle: String = "Video"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Parse video details from intent
        videoPath = intent.getStringExtra(EXTRA_VIDEO_PATH)
            ?: intent.data?.toString()
            ?: ""
        videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: videoPath
        videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE)
            ?: intent.data?.lastPathSegment
            ?: "Video Playback"

        val playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L)
        val playlistIndex = intent.getIntExtra(EXTRA_PLAYLIST_INDEX, 0)

        if (playlistId != -1L) {
            lifecycleScope.launch {
                val items = playlistRepository.getPlaylistItems(playlistId).firstOrNull()
                if (!items.isNullOrEmpty()) {
                    viewModel.setPlaylist(items, playlistIndex)
                } else {
                    viewModel.setCurrentVideoDetails(videoId, videoTitle)
                }
            }
        } else {
            viewModel.setCurrentVideoDetails(videoId, videoTitle)
        }

        MpvTeardownCoordinator.markActivityCoreInitialized()

        setContent {
            FinalPlayerTheme {
                PlayerScreen(
                    videoPath = videoPath,
                    videoTitle = videoTitle,
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder().build()
                enterPictureInPictureMode(params)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to enter Picture-in-Picture mode", e)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            val mpvView = viewModel.mpvController.getAttachedView()
            if (mpvView != null && mpvView.onKey(event)) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        val mpvView = viewModel.mpvController.getAttachedView()
        MpvTeardownCoordinator.destroyActivityCoreAsync("PlayerActivity onDestroy", mpvView)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "PlayerActivity"
        const val EXTRA_VIDEO_ID = "EXTRA_VIDEO_ID"
        const val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
        const val EXTRA_VIDEO_TITLE = "EXTRA_VIDEO_TITLE"
        const val EXTRA_PLAYLIST_ID = "EXTRA_PLAYLIST_ID"
        const val EXTRA_PLAYLIST_INDEX = "EXTRA_PLAYLIST_INDEX"
    }
}
