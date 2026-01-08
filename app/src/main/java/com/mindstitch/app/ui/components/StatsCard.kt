package com.mindstitch.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mindstitch.app.ui.theme.Emerald
import com.mindstitch.app.ui.theme.Orange
import com.mindstitch.app.ui.theme.Primary

data class StatData(
    val title: String,
    val value: String,
    val subtitle: String = "",
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun StatsCard(
    stat: StatData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (stat.subtitle.isNotEmpty()) {
                    Text(
                        text = stat.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

fun sampleStats(): List<StatData> = listOf(
    StatData(
        title = "TOTAL",
        value = "142",
        icon = Icons.Default.Lightbulb,
        iconTint = Primary
    ),
    StatData(
        title = "STREAK",
        value = "12",
        subtitle = "days",
        icon = Icons.Default.LocalFireDepartment,
        iconTint = Orange
    ),
    StatData(
        title = "TOP TAG",
        value = "Code",
        icon = Icons.Default.Tag,
        iconTint = Emerald
    )
)
