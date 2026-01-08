package com.mindstitch.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class IdeaRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).ideaDao()

    fun getAllIdeas(): Flow<List<IdeaEntity>> = dao.getAllIdeas()

    fun searchIdeas(query: String): Flow<List<IdeaEntity>> = dao.searchIdeas(query)

    suspend fun getIdeaById(id: Long): IdeaEntity? = dao.getIdeaById(id)

    suspend fun insert(idea: IdeaEntity): Long = dao.insert(idea)

    suspend fun update(idea: IdeaEntity) = dao.update(idea)

    suspend fun delete(idea: IdeaEntity) = dao.delete(idea)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun getFolders(): Flow<List<String>> = dao.getFolders()

    fun getIdeasByFolder(folder: String): Flow<List<IdeaEntity>> = dao.getIdeasByFolder(folder)

    // 获取热力图数据：最近12周，每周7天
    suspend fun getHeatmapData(): List<List<Int>> {
        val calendar = java.util.Calendar.getInstance()
        // 回溯12周 = 84天
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -84)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        
        val startTime = calendar.timeInMillis
        val ideas = dao.getIdeasSince(startTime)
        
        // 按日期分组统计
        val dailyCounts = mutableMapOf<String, Int>()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        ideas.forEach { idea ->
            val dateKey = dateFormat.format(java.util.Date(idea.createdAt))
            dailyCounts[dateKey] = (dailyCounts[dateKey] ?: 0) + 1
        }
        
        // 构建12周数据
        val weeks = mutableListOf<List<Int>>()
        calendar.timeInMillis = startTime
        
        // 调整到周一开始
        while (calendar.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.MONDAY) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        
        repeat(12) {
            val week = mutableListOf<Int>()
            repeat(7) {
                val dateKey = dateFormat.format(calendar.time)
                val count = dailyCounts[dateKey] ?: 0
                // 转换为0-5等级: 0=无, 1=1个, 2=2个, 3=3-4个, 4=5+个, 5=10+个
                val level = when {
                    count == 0 -> 0
                    count == 1 -> 1
                    count == 2 -> 2
                    count <= 4 -> 3
                    count < 10 -> 4
                    else -> 5
                }
                week.add(level)
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            weeks.add(week)
        }
        
        return weeks
    }

    // 获取日历数据：当月每天的总星级
    suspend fun getCalendarData(year: Int, month: Int): Map<Int, Int> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.MONTH, 1)
        val endTime = calendar.timeInMillis
        
        val ideas = dao.getIdeasByDateRange(startTime, endTime)
        
        // 按日期分组计算总星级
        val dailyRatings = mutableMapOf<Int, Int>()
        val cal = java.util.Calendar.getInstance()
        ideas.forEach { idea ->
            cal.timeInMillis = idea.createdAt
            val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
            dailyRatings[day] = (dailyRatings[day] ?: 0) + idea.rating
        }
        
        return dailyRatings
    }

    // 获取某天的所有idea
    suspend fun getIdeasByDate(year: Int, month: Int, day: Int): List<IdeaEntity> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val endTime = calendar.timeInMillis
        
        return dao.getIdeasByDateRange(startTime, endTime)
    }

    companion object {
        @Volatile
        private var INSTANCE: IdeaRepository? = null

        fun getInstance(context: Context): IdeaRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = IdeaRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
