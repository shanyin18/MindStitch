package com.mindstitch.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindstitch.app.ui.theme.Primary

// 活动等级：0=无活动，1-4=递增活动强度
typealias ActivityLevel = Int

@Composable
fun HeatmapGrid(
    weeks: List<List<ActivityLevel>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Map",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                HeatmapLegend()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Day labels
                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Mon", "", "Wed", "", "Fri", "", "Sun").forEach { day ->
                        Box(
                            modifier = Modifier.size(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Weeks
                weeks.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        week.forEach { level ->
                            HeatmapCell(level = level)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    level: ActivityLevel,
    modifier: Modifier = Modifier
) {
    val color = when (level) {
        0 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        1 -> Primary.copy(alpha = 0.2f)
        2 -> Primary.copy(alpha = 0.4f)
        3 -> Primary.copy(alpha = 0.6f)
        4 -> Primary.copy(alpha = 0.8f)
        else -> Primary
    }
    
    Box(
        modifier = modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun HeatmapLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Less",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        (0..4).forEach { level ->
            HeatmapCell(level = level)
        }
        Text(
            text = "More",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Sample heatmap data
fun sampleHeatmapData(): List<List<ActivityLevel>> = listOf(
    listOf(0, 0, 1, 0, 5, 0, 0),
    listOf(2, 0, 0, 3, 3, 0, 0),
    listOf(0, 0, 0, 0, 5, 5, 2),
    listOf(1, 1, 0, 0, 0, 0, 0),
    listOf(0, 0, 5, 4, 4, 0, 0),
    listOf(0, 2, 1, 0, 0, 0, 2),
    listOf(0, 0, 0, 0, 0, 4, 5),
    listOf(0, 0, 1, 1, 0, 0, 0),
    listOf(5, 4, 3, 0, 0, 0, 0),
    listOf(0, 0, 0, 0, 5, 2, 1),
    listOf(0, 0, 0, 0, 0, 0, 0),
    listOf(1, 1, 2, 4, 5, 0, 0)
)
