package com.mindstitch.app.data

import android.content.Context
import java.util.Calendar

class TodoRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).todoDao()

    // 获取某天的待办
    suspend fun getTodosByDate(year: Int, month: Int, day: Int): List<TodoEntity> {
        val (startTime, endTime) = getDayRange(year, month, day)
        return dao.getTodosByDateRange(startTime, endTime)
    }

    // 获取某月每天的未完成待办数量
    suspend fun getMonthTodoCounts(year: Int, month: Int): Map<Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endTime = calendar.timeInMillis

        val todos = dao.getTodosByDateRange(startTime, endTime)
        
        val dailyCounts = mutableMapOf<Int, Int>()
        val cal = Calendar.getInstance()
        todos.filter { !it.isCompleted }.forEach { todo ->
            cal.timeInMillis = todo.date
            val day = cal.get(Calendar.DAY_OF_MONTH)
            dailyCounts[day] = (dailyCounts[day] ?: 0) + 1
        }
        
        return dailyCounts
    }

    suspend fun insert(todo: TodoEntity): Long = dao.insert(todo)

    suspend fun update(todo: TodoEntity) = dao.update(todo)

    suspend fun delete(todo: TodoEntity) = dao.delete(todo)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    // 切换完成状态
    suspend fun toggleComplete(todo: TodoEntity) {
        dao.update(todo.copy(isCompleted = !todo.isCompleted))
    }

    private fun getDayRange(year: Int, month: Int, day: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }

    // 创建待办（自动设置正确的日期时间戳）
    suspend fun createTodo(title: String, year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        return dao.insert(TodoEntity(
            title = title,
            date = calendar.timeInMillis
        ))
    }

    companion object {
        @Volatile
        private var INSTANCE: TodoRepository? = null

        fun getInstance(context: Context): TodoRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = TodoRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
