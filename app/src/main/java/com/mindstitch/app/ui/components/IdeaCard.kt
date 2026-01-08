package com.mindstitch.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mindstitch.app.ui.theme.*

enum class IdeaCardType {
    TEXT,
    IMAGE,
    QUOTE,
    SEED,
    HIGHLIGHT
}

data class IdeaData(
    val id: String,
    val title: String,
    val content: String = "",
    val type: IdeaCardType = IdeaCardType.TEXT,
    val icon: ImageVector? = null,
    val iconTint: Color = Emerald,
    val tags: List<Pair<String, Color>> = emptyList(),
    val timeAgo: String = "",
    val imageUrl: String? = null,
    val author: String? = null,
    val backgroundColor: Color? = null,
    val rating: Int = 0,      // 1-5星
    val upCount: Int = 0      // UP次数
)

@Composable
fun IdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (idea.type) {
        IdeaCardType.TEXT -> TextIdeaCard(idea, onClick, modifier)
        IdeaCardType.IMAGE -> ImageIdeaCard(idea, onClick, modifier)
        IdeaCardType.QUOTE -> QuoteIdeaCard(idea, onClick, modifier)
        IdeaCardType.SEED -> SeedIdeaCard(idea, onClick, modifier)
        IdeaCardType.HIGHLIGHT -> HighlightIdeaCard(idea, onClick, modifier)
    }
}

@Composable
private fun TextIdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                idea.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = idea.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (idea.timeAgo.isNotEmpty()) {
                    Text(
                        text = idea.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (idea.content.isNotEmpty()) {
                    Text(
                        text = idea.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (idea.tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    idea.tags.take(2).forEach { (tag, color) ->
                        TagChip(tag = tag, color = color)
                    }
                }
            }
            
            // 星级 + UP进度条
            if (idea.rating > 0 || idea.upCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 星级显示
                    if (idea.rating > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(idea.rating) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    
                    // UP进度条 + 次数
                    if (idea.upCount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val upAlpha = (0.3f + (idea.upCount.coerceAtMost(50) / 50f) * 0.7f).coerceIn(0.3f, 1f)
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((idea.upCount.coerceAtMost(100) / 100f).coerceAtLeast(0.1f))
                                        .fillMaxHeight()
                                        .background(Indigo.copy(alpha = upAlpha))
                                )
                            }
                            Text(
                                text = "×${idea.upCount}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Indigo.copy(alpha = upAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageIdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                idea.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = idea.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                idea.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (idea.content.isNotEmpty()) {
                    Text(
                        text = idea.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (idea.tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        idea.tags.forEach { (tag, color) ->
                            TagChip(tag = tag, color = color)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteIdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = idea.backgroundColor ?: Color(0xFF2E291E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = Amber,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "\"${idea.content}\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = Color(0xFFFEF3C7)
            )
            idea.author?.let { author ->
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFFD97706)
                )
            }
        }
    }
}

@Composable
private fun SeedIdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier.size(24.dp)
                )
                if (idea.timeAgo.isNotEmpty()) {
                    Text(
                        text = idea.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (idea.content.isNotEmpty()) {
                    Text(
                        text = idea.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (idea.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    idea.tags.forEach { (tag, color) ->
                        TagChip(tag = tag, color = color)
                    }
                }
            }
        }
    }
}

@Composable
private fun HighlightIdeaCard(
    idea: IdeaData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    Color.Transparent
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = Indigo,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = idea.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (idea.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    idea.tags.forEach { (tag, color) ->
                        TagChip(tag = tag, color = color)
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = "#$tag",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Sample data for preview
fun sampleIdeas(): List<IdeaData> = listOf(
    IdeaData(
        id = "1",
        title = "App Architecture",
        content = "Need to switch to MVVM pattern for the new iOS module to ensure better testability.",
        type = IdeaCardType.TEXT,
        icon = Icons.Default.Park,
        iconTint = Emerald,
        tags = listOf("Dev" to Blue, "Tech" to Purple),
        timeAgo = "2h ago"
    ),
    IdeaData(
        id = "2",
        title = "Gift Ideas",
        content = "Mom likes vintage watches from the 70s...",
        type = IdeaCardType.IMAGE,
        icon = Icons.Default.Spa,
        iconTint = Amber,
        tags = listOf("Personal" to Rose),
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCHqGfMg8a4miAAOm_3PCtrw6sA8JBBuP-eFBfpFpNfAXBV4z4TVtGTNVODEscw5dI4LXP1tLzLu6AmsPiGIKEhweZ0oxX5aqzNCIpbtkRASrK7ehP5MLOMcwjnn6KhpbAu7xBsj8k-skqDCzL71lWtJA1sa0SJeYlOnExF9mq8yphd3otZ5EIBsrA09KvwMaveSWoTswRa-UWo-EDdWeP8_efq9MYDdAheamnnEv-nLZ8AJtXKI87aAmkXAldHNqxY14Vz_lwfLQow"
    ),
    IdeaData(
        id = "3",
        title = "Podcast Theme",
        type = IdeaCardType.HIGHLIGHT,
        tags = listOf("Content" to Indigo)
    ),
    IdeaData(
        id = "4",
        title = "Novel Opening",
        content = "The rain didn't wash away the memories, it only made them slicker, harder to hold onto. He stood by the window watching the neon sign flicker...",
        type = IdeaCardType.SEED,
        tags = listOf("Writing" to Cyan, "Draft" to Color.Gray),
        timeAgo = "1d ago"
    ),
    IdeaData(
        id = "5",
        title = "",
        content = "Creativity is intelligence having fun.",
        type = IdeaCardType.QUOTE,
        author = "Einstein"
    ),
    IdeaData(
        id = "6",
        title = "Color Palette",
        type = IdeaCardType.IMAGE,
        icon = Icons.Default.Image,
        tags = listOf("Art" to Color.White),
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA8ICE8sSN7B_FO33GRWdrUUPfXtfhJnoKjQHH5mD6LUuXiX6dwm82c31f5VlKjxwl3-Siqp5Av-q2DcNImk0gl4d3UJZbtGbthMJiweXzBykWFlNCSn86zBWkOl99c8GL-V8_ZuBNFZFKJznIle56Erw42VvU4OfnS_x7VOFhmPazEx_D3O7Vu6s0uko1sGS8hRvutOPKxOY8NvqM5_HyI0OZKRCc9ZPAAd0DMzYtghbuJ5vIYPdsMKDrBcuc66vEqSl4APiJkquvU"
    )
)
