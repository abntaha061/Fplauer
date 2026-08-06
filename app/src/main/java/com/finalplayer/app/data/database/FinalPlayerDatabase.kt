package com.finalplayer.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finalplayer.app.data.database.dao.NetworkSourceDao
import com.finalplayer.app.data.database.dao.PlaybackProgressDao
import com.finalplayer.app.data.database.dao.PlaylistDao
import com.finalplayer.app.data.database.dao.VideoDao
import com.finalplayer.app.data.database.entities.FolderScanEntity
import com.finalplayer.app.data.database.entities.NetworkSourceEntity
import com.finalplayer.app.data.database.entities.PlaybackProgressEntity
import com.finalplayer.app.data.database.entities.PlaylistEntity
import com.finalplayer.app.data.database.entities.PlaylistItemEntity
import com.finalplayer.app.data.database.entities.VideoEntity

@Database(
    entities = [
        VideoEntity::class,
        FolderScanEntity::class,
        PlaybackProgressEntity::class,
        PlaylistEntity::class,
        PlaylistItemEntity::class,
        NetworkSourceEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FinalPlayerDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun networkSourceDao(): NetworkSourceDao
}
