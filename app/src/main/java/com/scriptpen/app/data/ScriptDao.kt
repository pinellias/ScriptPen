package com.scriptpen.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {

    @Query("SELECT * FROM scripts ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ScriptDocument>>

    @Query("SELECT * FROM scripts WHERE id = :id")
    fun observeById(id: Long): Flow<ScriptDocument?>

    @Query("SELECT * FROM scripts WHERE id = :id")
    suspend fun getById(id: Long): ScriptDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: ScriptDocument): Long

    @Update
    suspend fun update(doc: ScriptDocument)

    @Delete
    suspend fun delete(doc: ScriptDocument)

    @Query("DELETE FROM scripts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
