package com.finalplayer.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finalplayer.app.data.database.entities.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY title ASC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE folderPath = :folderPath ORDER BY title ASC")
    fun getVideosByFolder(folderPath: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)

    @Query("DELETE FROM videos WHERE folderPath = :folderPath")
    suspend fun deleteAllVideosInFolder(folderPath: String)

    @Query("SELECT DISTINCT folderPath FROM videos")
    fun getDistinctFolders(): Flow<List<String>>
}
