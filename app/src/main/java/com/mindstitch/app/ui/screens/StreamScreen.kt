package com.mindstitch.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mindstitch.app.data.IdeaEntity
import com.mindstitch.app.data.IdeaRepository
import com.mindstitch.app.ui.components.*
import com.mindstitch.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StreamScreen(
    onNavigateToCapture: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStitch: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { IdeaRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    
    // Observe folders from database
    val folders by repository.getFolders().collectAsState(initial = emptyList())
    var selectedFolder by remember { mutableStateOf<String?>(null) } // null means "All"
    
    // Build filter chips from folders
    val filterChips = remember(folders, selectedFolder) {
        val allChip = FilterChip(label = "全部", isSelected = selectedFolder == null)
        val folderChips = folders.map { folder ->
            FilterChip(label = folder, isSelected = selectedFolder == folder)
        }
        listOf(allChip) + folderChips
    }
    
    val ideas by remember(searchQuery.text, selectedFolder) {
        when {
            searchQuery.text.isNotBlank() -> repository.searchIdeas(searchQuery.text)
            selectedFolder != null -> repository.getIdeasByFolder(selectedFolder!!)
            else -> repository.getAllIdeas()
        }
    }.collectAsState(initial = emptyList())
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var ideaToDelete by remember { mutableStateOf<IdeaEntity?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (showDeleteDialog && ideaToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除灵感") },
            text = { Text("确定要删除「${ideaToDelete?.title}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            ideaToDelete?.let { repository.delete(it) }
                        }
                        showDeleteDialog = false
                        ideaToDelete = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )
                FilterChips(
                    chips = filterChips,
                    onChipClick = { index ->
                        // index 0 = "全部", index 1+ = folder names
                        selectedFolder = if (index == 0) null else folders.getOrNull(index - 1)
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCapture,
                modifier = Modifier.padding(bottom = 16.dp),
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新建",
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = "stream",
                onNavigate = { item ->
                    when (item) {
                        BottomNavItem.Stream -> { }
                        BottomNavItem.Spaces -> { }
                        BottomNavItem.Stitch -> onNavigateToStitch()
                        BottomNavItem.Profile -> onNavigateToStats()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (ideas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "✨", style = MaterialTheme.typography.displayLarge)
                    Text(
                        text = if (searchQuery.text.isBlank()) "还没有灵感" else "没有找到匹配的灵感",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (searchQuery.text.isBlank()) {
                        Text(
                            text = "点击右下角按钮记录你的第一个想法",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(ideas, key = { it.id }) { idea ->
                    val ideaData = idea.toIdeaData()
                    IdeaCard(
                        idea = ideaData,
                        onClick = { onNavigateToDetail(idea.id.toString()) },
                        modifier = Modifier.combinedClickable(
                            onClick = { onNavigateToDetail(idea.id.toString()) },
                            onLongClick = {
                                ideaToDelete = idea
                                showDeleteDialog = true
                            }
                        )
                    )
                }
            }
        }
    }
}

fun IdeaEntity.toIdeaData(): IdeaData {
    val tagList = if (tags.isNotBlank()) {
        tags.split(",").map { it.trim() to Primary }
    } else {
        emptyList()
    }
    
    val now = System.currentTimeMillis()
    val diff = now - createdAt
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24
    val timeAgo = when {
        days > 0 -> "${days}天前"
        hours > 0 -> "${hours}小时前"
        minutes > 0 -> "${minutes}分钟前"
        else -> "刚刚"
    }
    
    return IdeaData(
        id = id.toString(),
        title = title,
        content = getTextContent(),  // 从内容块提取文字
        type = if (hasImages()) IdeaCardType.TEXT else IdeaCardType.TEXT,
        tags = tagList,
        timeAgo = timeAgo,
        rating = rating,
        upCount = upCount
    )
}
