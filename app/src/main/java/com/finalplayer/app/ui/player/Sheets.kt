package com.finalplayer.app.ui.player

sealed class Sheets {
    object None : Sheets()
    object SubtitleTracks : Sheets()
    object AudioTracks : Sheets()
    object Chapters : Sheets()
    object Decoders : Sheets()
    object PlaybackSpeed : Sheets()
    object More : Sheets()
    object SleepTimer : Sheets()
    object Playlist : Sheets()
}
