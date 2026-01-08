package com.mindstitch.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindstitch.app.data.IdeaEntity
import com.mindstitch.app.data.IdeaRepository
import com.mindstitch.app.data.TodoEntity
import com.mindstitch.app.data.TodoRepository
import com.mindstitch.app.ui.components.*
import com.mindstitch.app.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStream: () -> Unit,
    onNavigateToCapture: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToSyncSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { IdeaRepository.getInstance(context) }
    val todoRepository = remember { TodoRepository.getInstance(context) }
    val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    
    val stats = remember { sampleStats() }
    val archivedItems = remember { sampleArchivedItems() }
    val scrollState = rememberScrollState()
    
    // 日历状态
    val today = remember { Calendar.getInstance() }
    var currentYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var calendarData by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var todoData by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    
    // 选中日期的idea和待办列表弹窗
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var dayIdeas by remember { mutableStateOf<List<IdeaEntity>>(emptyList()) }
    var dayTodos by remember { mutableStateOf<List<TodoEntity>>(emptyList()) }
    var showDayDialog by remember { mutableStateOf(false) }
    var newTodoText by remember { mutableStateOf("") }
    
    // 加载日历数据
    LaunchedEffect(currentYear, currentMonth) {
        calendarData = repository.getCalendarData(currentYear, currentMonth)
        todoData = todoRepository.getMonthTodoCounts(currentYear, currentMonth)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The Universe",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSyncSettings) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Cloud Sync"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // Floating bottom nav
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.1f),
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingNavButton(
                            icon = Icons.Outlined.Home,
                            isSelected = false,
                            onClick = onNavigateToStream
                        )
                        FloatingNavButton(
                            icon = Icons.Outlined.AddCircle,
                            isSelected = false,
                            onClick = onNavigateToCapture
                        )
                        FloatingNavButton(
                            icon = Icons.Default.Analytics,
                            isSelected = true,
                            onClick = { }
                        )
                        FloatingNavButton(
                            icon = Icons.Outlined.Person,
                            isSelected = false,
                            onClick = onNavigateToProfile
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Stats cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.forEach { stat ->
                    StatsCard(stat = stat)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 日历视图
            IdeaCalendar(
                year = currentYear,
                month = currentMonth,
                dailyRatings = calendarData,
                dailyTodos = todoData,
                onDayClick = { day ->
                    selectedDay = day
                    scope.launch {
                        dayIdeas = repository.getIdeasByDate(currentYear, currentMonth, day)
                        dayTodos = todoRepository.getTodosByDate(currentYear, currentMonth, day)
                        newTodoText = ""
                        showDayDialog = true
                    }
                },
                onPrevMonth = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                },
                onNextMonth = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Radar chart
            RadarChartSection(
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Archived list
            ArchivedSection(
                items = archivedItems,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
    
    // 当天idea列表弹窗
    if (showDayDialog && selectedDay != null) {
        AlertDialog(
            onDismissRequest = { showDayDialog = false },
            title = {
                Text(
                    text = "${currentMonth + 1}月${selectedDay}日",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. 待办事项区域
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "待办事项",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary
                        )
                        
                        // 添加待办输入框
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BasicTextField(
                                value = newTodoText,
                                onValueChange = { newTodoText = it },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (newTodoText.isEmpty()) {
                                            Text(
                                                "添加新待办...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            IconButton(
                                onClick = { 
                                    if (newTodoText.isNotBlank()) {
                                        scope.launch {
                                            todoRepository.createTodo(newTodoText, currentYear, currentMonth, selectedDay!!)
                                            // 刷新数据
                                            dayTodos = todoRepository.getTodosByDate(currentYear, currentMonth, selectedDay!!)
                                            todoData = todoRepository.getMonthTodoCounts(currentYear, currentMonth)
                                            newTodoText = ""
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Primary, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        }

                        // 待办列表
                        if (dayTodos.isEmpty()) {
                            Text(
                                "暂无待办", 
                                style = MaterialTheme.typography.bodySmall, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            // 使用LazyColumn如果列表很长，这里用Column简单处理
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .heightIn(max = 150.dp)
                            ) {
                                dayTodos.forEach { todo ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Checkbox(
                                                checked = todo.isCompleted,
                                                onCheckedChange = { 
                                                    scope.launch {
                                                        todoRepository.toggleComplete(todo)
                                                        dayTodos = todoRepository.getTodosByDate(currentYear, currentMonth, selectedDay!!)
                                                        todoData = todoRepository.getMonthTodoCounts(currentYear, currentMonth)
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = todo.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    textDecoration = if (todo.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                                    color = if (todo.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                                ),
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { 
                                                scope.launch {
                                                    todoRepository.delete(todo)
                                                    dayTodos = todoRepository.getTodosByDate(currentYear, currentMonth, selectedDay!!)
                                                    todoData = todoRepository.getMonthTodoCounts(currentYear, currentMonth)
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete, 
                                                null, 
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    // 2. 想法记录区域
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "想法记录",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary
                        )
                        
                        if (dayIdeas.isEmpty()) {
                            Text(
                                "这天没有记录想法", 
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                dayIdeas.forEach { idea ->
                                    Surface(
                                        onClick = { 
                                            showDayDialog = false
                                            onNavigateToDetail(idea.id)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = idea.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = idea.folder,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (idea.rating > 0) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                                                    Text(idea.rating.toString(), style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFD700))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDayDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
}

@Composable
private fun FloatingNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .then(
                if (isSelected) {
                    Modifier.background(Primary, CircleShape)
                } else {
                    Modifier
                }
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RadarChartSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tag Distribution",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )

            // Radar chart canvas
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                RadarChart(
                    data = listOf(0.9f, 0.7f, 0.5f, 0.6f, 0.8f),
                    labels = listOf("Code", "Life", "Math", "Design", "Writing")
                )
            }
        }
    }
}

@Composable
private fun RadarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val primaryColor = Primary
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.8f
        val sides = data.size
        val angleStep = (2 * Math.PI / sides).toFloat()
        val startAngle = (-Math.PI / 2).toFloat()

        // Draw grid levels
        for (level in 1..4) {
            val levelRadius = radius * (level / 4f)
            val gridPath = Path()
            for (i in 0 until sides) {
                val angle = startAngle + i * angleStep
                val x = centerX + levelRadius * cos(angle)
                val y = centerY + levelRadius * sin(angle)
                if (i == 0) {
                    gridPath.moveTo(x, y)
                } else {
                    gridPath.lineTo(x, y)
                }
            }
            gridPath.close()
            drawPath(
                path = gridPath,
                color = Color.White.copy(alpha = 0.1f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw spokes
        for (i in 0 until sides) {
            val angle = startAngle + i * angleStep
            val endX = centerX + radius * cos(angle)
            val endY = centerY + radius * sin(angle)
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw data polygon
        val dataPath = Path()
        for (i in data.indices) {
            val angle = startAngle + i * angleStep
            val dataRadius = radius * data[i]
            val x = centerX + dataRadius * cos(angle)
            val y = centerY + dataRadius * sin(angle)
            if (i == 0) {
                dataPath.moveTo(x, y)
            } else {
                dataPath.lineTo(x, y)
            }
        }
        dataPath.close()

        // Fill
        drawPath(
            path = dataPath,
            color = primaryColor.copy(alpha = 0.2f)
        )
        // Stroke
        drawPath(
            path = dataPath,
            color = primaryColor,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw data points
        for (i in data.indices) {
            val angle = startAngle + i * angleStep
            val dataRadius = radius * data[i]
            val x = centerX + dataRadius * cos(angle)
            val y = centerY + dataRadius * sin(angle)
            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Draw labels
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#9CA3AF")
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        for (i in labels.indices) {
            val angle = startAngle + i * angleStep
            val labelRadius = radius * 1.25f
            val x = centerX + labelRadius * cos(angle)
            val y = centerY + labelRadius * sin(angle) + 4.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText(labels[i], x, y, paint)
        }
    }
}

@Composable
private fun ArchivedSection(
    items: List<ArchivedItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Archived",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = { }) {
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary
                )
            }
        }

        items.forEach { item ->
            ArchivedItemCard(item = item)
        }
    }
}

data class ArchivedItem(
    val title: String,
    val date: String,
    val ideaCount: Int,
    val tag: String,
    val tagColor: Color
)

@Composable
private fun ArchivedItemCard(item: ArchivedItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.date} • ${item.ideaCount} ideas inside",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = item.tagColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = item.tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = item.tagColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

fun sampleArchivedItems(): List<ArchivedItem> = listOf(
    ArchivedItem(
        title = "Neural Net Optimization",
        date = "Oct 12",
        ideaCount = 3,
        tag = "Code",
        tagColor = Primary
    ),
    ArchivedItem(
        title = "Balcony Garden Layout",
        date = "Sep 28",
        ideaCount = 12,
        tag = "Life",
        tagColor = Green
    ),
    ArchivedItem(
        title = "Fractal Generator",
        date = "Aug 05",
        ideaCount = 1,
        tag = "Math",
        tagColor = Purple
    ),
    ArchivedItem(
        title = "Character Arcs",
        date = "July 22",
        ideaCount = 8,
        tag = "Writing",
        tagColor = Orange
    )
)
