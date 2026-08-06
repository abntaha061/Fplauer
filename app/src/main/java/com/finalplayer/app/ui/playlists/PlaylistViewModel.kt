package com.finalplayer.app.ui.playlists

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalplayer.app.domain.model.PlaylistWithItems
import com.finalplayer.app.domain.model.VideoItem
import com.finalplayer.app.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlaylistViewModel : ViewModel(), KoinComponent {
    private val playlistRepository: PlaylistRepository by inject()

    val playlists: StateFlow<List<PlaylistWithItems>> = playlistRepository.playlists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                playlistRepository.createPlaylist(name)
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun addVideoToPlaylist(playlistId: Long, videoId: String) {
        viewModelScope.launch {
            playlistRepository.addVideoToPlaylist(playlistId, videoId)
        }
    }

    fun removeVideoFromPlaylist(playlistId: Long, videoId: String) {
        viewModelScope.launch {
            playlistRepository.removeVideoFromPlaylist(playlistId, videoId)
        }
    }

    fun getPlaylistItems(playlistId: Long): Flow<List<VideoItem>> {
        return playlistRepository.getPlaylistItems(playlistId)
    }

    fun reorderItems(playlistId: Long, from: Int, to: Int) {
        viewModelScope.launch {
            playlistRepository.reorderItems(playlistId, from, to)
        }
    }

    fun importFromM3U(uri: Uri) {
        viewModelScope.launch {
            playlistRepository.importFromM3U(uri)
        }
    }
}
