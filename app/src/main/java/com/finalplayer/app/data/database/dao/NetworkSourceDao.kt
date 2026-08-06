package com.finalplayer.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finalplayer.app.data.database.entities.NetworkSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkSourceDao {
    @Query("SELECT * FROM network_sources")
    fun getAllSources(): Flow<List<NetworkSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: NetworkSourceEntity)

    @Query("DELETE FROM network_sources WHERE id = :sourceId")
    suspend fun deleteSource(sourceId: String)
}
