package com.refresh.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.refresh.pos.data.local.entity.LanguageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {

    @Query("SELECT * FROM language LIMIT 1")
    fun get(): Flow<LanguageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(language: LanguageEntity)
}
