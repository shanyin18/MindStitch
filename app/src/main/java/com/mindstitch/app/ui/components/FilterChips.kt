package com.mindstitch.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mindstitch.app.ui.theme.Orange

data class FilterChip(
    val label: String,
    val icon: ImageVector? = null,
    val iconTint: Color? = null,
    val isSelected: Boolean = false
)

@Composable
fun FilterChips(
    chips: List<FilterChip>,
    onChipClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEachIndexed { index, chip ->
            FilterChipItem(
                chip = chip,
                onClick = { onChipClick(index) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    chip: FilterChip,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (chip.isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        shadowElevation = if (chip.isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = chip.label,
                style = MaterialTheme.typography.labelLarge,
                color = if (chip.isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            chip.icon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = chip.iconTint ?: if (chip.isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun defaultFilterChips(): List<FilterChip> = listOf(
    FilterChip(label = "Recent", isSelected = true),
    FilterChip(label = "Heat", icon = Icons.Default.LocalFireDepartment, iconTint = Orange),
    FilterChip(label = "Tags"),
    FilterChip(label = "Voice"),
    FilterChip(label = "Images")
)
