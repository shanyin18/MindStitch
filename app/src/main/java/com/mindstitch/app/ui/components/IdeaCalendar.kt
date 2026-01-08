package com.mindstitch.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindstitch.app.ui.theme.Primary
import java.util.Calendar

@Composable
fun IdeaCalendar(
    year: Int,
    month: Int,  // 0-based (Calendar.JANUARY = 0)
    dailyRatings: Map<Int, Int>,  // day -> total rating
    dailyTodos: Map<Int, Int> = emptyMap(),  // day -> uncompleted todo count
    onDayClick: (Int) -> Unit,  // day clicked
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance().apply {
        set(year, month, 1)
    }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    
    val monthNames = listOf("一月", "二月", "三月", "四月", "五月", "六月", 
                            "七月", "八月", "九月", "十月", "十一月", "十二月")
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
    
    val today = Calendar.getInstance()
    val isCurrentMonth = today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month
    val currentDay = today.get(Calendar.DAY_OF_MONTH)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 月份导航
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(Icons.Default.ChevronLeft, "上月", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = "${year}年 ${monthNames[month]}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ChevronRight, "下月", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 星期标题
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 日历格子
            var dayCounter = 1
            for (week in 0..5) {
                if (dayCounter > daysInMonth) break
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayOfWeek in 0..6) {
                        val cellIndex = week * 7 + dayOfWeek
                        if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                            // 空白格子
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val day = dayCounter
                            val rating = dailyRatings[day] ?: 0
                            val todoCount = dailyTodos[day] ?: 0
                            val isToday = isCurrentMonth && day == currentDay
                            
                            CalendarDayCell(
                                day = day,
                                totalRating = rating,
                                todoCount = todoCount,
                                isToday = isToday,
                                onClick = { onDayClick(day) },
                                modifier = Modifier.weight(1f)
                            )
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    totalRating: Int,
    todoCount: Int = 0,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasContent = totalRating > 0 || todoCount > 0
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isToday -> Primary.copy(alpha = 0.2f)
                    hasContent -> MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 待办红点（右上角）
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    color = if (isToday) Primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
                if (todoCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .background(Color(0xFFFF6B6B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (todoCount > 9) "9+" else todoCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                            color = Color.White
                        )
                    }
                }
            }
            
            if (totalRating > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = totalRating.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}
