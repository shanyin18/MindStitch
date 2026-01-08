package com.mindstitch.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {
    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE title LIKE '%' || :query || '%' OR contentBlocks LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchIdeas(query: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE id = :id")
    suspend fun getIdeaById(id: Long): IdeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(idea: IdeaEntity): Long

    @Update
    suspend fun update(idea: IdeaEntity)

    @Delete
    suspend fun delete(idea: IdeaEntity)

    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT DISTINCT folder FROM ideas ORDER BY folder ASC")
    fun getFolders(): Flow<List<String>>

    @Query("SELECT * FROM ideas WHERE folder = :folder ORDER BY createdAt DESC")
    fun getIdeasByFolder(folder: String): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE createdAt >= :startTime ORDER BY createdAt DESC")
    suspend fun getIdeasSince(startTime: Long): List<IdeaEntity>

    @Query("SELECT * FROM ideas WHERE createdAt >= :startTime AND createdAt < :endTime ORDER BY createdAt DESC")
    suspend fun getIdeasByDateRange(startTime: Long, endTime: Long): List<IdeaEntity>
}
