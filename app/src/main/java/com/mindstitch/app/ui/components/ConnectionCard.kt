package com.mindstitch.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mindstitch.app.ui.theme.Emerald
import com.mindstitch.app.ui.theme.Amber

data class ConnectionData(
    val id: String,
    val title: String,
    val description: String,
    val matchPercentage: Int
)

@Composable
fun ConnectionCard(
    connection: ConnectionData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val matchColor = when {
        connection.matchPercentage >= 80 -> Emerald
        connection.matchPercentage >= 60 -> Amber
        else -> Color.Gray
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = connection.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = matchColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${connection.matchPercentage}% Match",
                        style = MaterialTheme.typography.labelSmall,
                        color = matchColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text = connection.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun sampleConnections(): List<ConnectionData> = listOf(
    ConnectionData(
        id = "1",
        title = "Project Alpha: Voice Notes",
        description = "Explores similar constraints regarding offline-first audio processing and local transcription.",
        matchPercentage = 85
    ),
    ConnectionData(
        id = "2",
        title = "Design System v2",
        description = "References the typography scale needed for high legibility in the journaling interface.",
        matchPercentage = 62
    )
)
