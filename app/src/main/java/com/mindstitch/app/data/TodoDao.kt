package com.mindstitch.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt ASC")
    suspend fun getAllTodos(): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE date >= :startTime AND date < :endTime ORDER BY createdAt ASC")
    suspend fun getTodosByDateRange(startTime: Long, endTime: Long): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE date >= :startTime AND date < :endTime ORDER BY createdAt ASC")
    fun getTodosByDateRangeFlow(startTime: Long, endTime: Long): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM todos WHERE date >= :startTime AND date < :endTime AND isCompleted = 0")
    suspend fun getUncompletedCount(startTime: Long, endTime: Long): Int
}
