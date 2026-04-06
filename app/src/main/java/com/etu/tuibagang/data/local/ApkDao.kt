package com.etu.tuibagang.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApkDao {
    @Query("SELECT * FROM apks ORDER BY createdAt DESC")
    fun observeApks(): Flow<List<ApkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ApkEntity>)
}
