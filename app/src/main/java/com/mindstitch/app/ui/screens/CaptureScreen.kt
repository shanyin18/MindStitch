package com.mindstitch.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mindstitch.app.data.IdeaEntity
import com.mindstitch.app.data.IdeaRepository
import com.mindstitch.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    onNavigateBack: () -> Unit,
    onSaveAndStitch: () -> Unit,
    editIdeaId: Long? = null
) {
    val context = LocalContext.current
    val repository = remember { IdeaRepository.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    var title by remember { mutableStateOf("") }
    var folder by remember { mutableStateOf("Default") }
    // 内容块列表 - 支持图文混排
    var contentBlocks by remember { mutableStateOf(listOf<com.mindstitch.app.data.ContentBlock>()) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(editIdeaId != null) }
    var existingIdea by remember { mutableStateOf<IdeaEntity?>(null) }
    
    // 评级和UP功能
    var rating by remember { mutableStateOf(0) }
    var upCount by remember { mutableStateOf(0) }
    
    // 当前要插入图片的位置索引 (-1 表示追加到末尾)
    var insertImageAtIndex by remember { mutableStateOf(-1) }
    
    // 裁剪后的临时 URI
    var pendingCropUri by remember { mutableStateOf<Uri?>(null) }
    
    // uCrop 裁剪结果处理 - 插入图片块
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.let { data ->
                val croppedUri = com.yalantis.ucrop.UCrop.getOutput(data)
                croppedUri?.let { uri ->
                    val imageBlock = com.mindstitch.app.data.ContentBlock.Image(
                        uri = uri.toString(),
                        scale = 1f,
                        align = "START"
                    )
                    contentBlocks = if (insertImageAtIndex >= 0 && insertImageAtIndex <= contentBlocks.size) {
                        contentBlocks.toMutableList().apply { add(insertImageAtIndex, imageBlock) }
                    } else {
                        contentBlocks + imageBlock
                    }
                    insertImageAtIndex = -1
                }
            }
        }
        pendingCropUri = null
    }
    
    // 图片选择器 -> 选完直接进裁剪
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val destFile = java.io.File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            val destUri = Uri.fromFile(destFile)
            
            val uCropIntent = com.yalantis.ucrop.UCrop.of(sourceUri, destUri)
                .withOptions(com.yalantis.ucrop.UCrop.Options().apply {
                    setFreeStyleCropEnabled(true)
                    setShowCropGrid(true)
                    setShowCropFrame(true)
                    setToolbarColor(android.graphics.Color.parseColor("#1A1A2E"))
                    setStatusBarColor(android.graphics.Color.parseColor("#0F0F1A"))
                    setToolbarWidgetColor(android.graphics.Color.WHITE)
                    setRootViewBackgroundColor(android.graphics.Color.parseColor("#1A1A2E"))
                    setCropFrameColor(android.graphics.Color.parseColor("#6366F1"))
                    setCropGridColor(android.graphics.Color.parseColor("#6366F1"))
                    setActiveControlsWidgetColor(android.graphics.Color.parseColor("#6366F1"))
                    setToolbarTitle("裁剪图片")
                })
                .getIntent(context)
            
            pendingCropUri = destUri
            cropLauncher.launch(uCropIntent)
        }
    }
    
    // 加载编辑的数据
    LaunchedEffect(editIdeaId) {
        if (editIdeaId != null) {
            val idea = repository.getIdeaById(editIdeaId)
            idea?.let {
                title = it.title
                folder = it.folder
                contentBlocks = it.getBlocks()
                rating = it.rating
                upCount = it.upCount
                existingIdea = it
            }
            isLoading = false
        }
    }
    
    val existingFolders by repository.getFolders().collectAsState(initial = emptyList())
    var showFolderPicker by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var showNewFolderInput by remember { mutableStateOf(false) }

    val isEditMode = editIdeaId != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isEditMode) Indigo.copy(alpha = 0.15f) 
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp)) {
                                    Surface(
                                        shape = CircleShape, 
                                        color = if (isEditMode) Indigo else Primary
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize())
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEditMode) "EDITING" else "DRAFTING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = if (isEditMode) Indigo 
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(Primary),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (title.isEmpty()) {
                                    Text(
                                        text = "What's the big idea?",
                                        style = TextStyle(
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextTertiaryDark
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ===== 简化编辑器：图文混排 =====
                    // 确保至少有一个文字块
                    LaunchedEffect(contentBlocks) {
                        if (contentBlocks.isEmpty()) {
                            contentBlocks = listOf(com.mindstitch.app.data.ContentBlock.Text(""))
                        }
                        // 确保最后一个块是文字块（方便继续输入）
                        val last = contentBlocks.lastOrNull()
                        if (last != null && last is com.mindstitch.app.data.ContentBlock.Image) {
                            contentBlocks = contentBlocks + com.mindstitch.app.data.ContentBlock.Text("")
                        }
                    }
                    
                    // 渲染内容块
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        contentBlocks.forEachIndexed { index, block ->
                            when (block) {
                                is com.mindstitch.app.data.ContentBlock.Text -> {
                                    // 文字块 - 可编辑
                                    var text by remember(index, block.content) { mutableStateOf(block.content) }
                                    BasicTextField(
                                        value = text,
                                        onValueChange = { newText ->
                                            text = newText
                                            contentBlocks = contentBlocks.toMutableList().also {
                                                it[index] = com.mindstitch.app.data.ContentBlock.Text(newText)
                                            }
                                        },
                                        textStyle = TextStyle(
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 28.sp
                                        ),
                                        cursorBrush = SolidColor(Primary),
                                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
                                        decorationBox = { innerTextField ->
                                            Box {
                                                if (text.isEmpty() && index == 0 && contentBlocks.size == 1) {
                                                    Text(
                                                        text = "开始输入内容...",
                                                        style = TextStyle(fontSize = 18.sp, color = TextTertiaryDark)
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                }
                                is com.mindstitch.app.data.ContentBlock.Image -> {
                                    // 图片块
                                    var localScale by remember(index) { mutableStateOf(block.scale) }
                                    val alignment = when (block.align) {
                                        "CENTER" -> Alignment.CenterHorizontally
                                        "END" -> Alignment.End
                                        else -> Alignment.Start
                                    }
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = alignment
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(localScale.coerceIn(0.3f, 1f))
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            AsyncImage(
                                                model = Uri.parse(block.uri),
                                                contentDescription = "Image",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                            )
                                            // 删除按钮
                                            IconButton(
                                                onClick = {
                                                    contentBlocks = contentBlocks.filterIndexed { i, _ -> i != index }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(24.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            ) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        // 缩放 + 对齐控制（紧凑版）
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Slider(
                                                value = localScale,
                                                onValueChange = { localScale = it },
                                                onValueChangeFinished = {
                                                    // 只在拖动结束时更新
                                                    contentBlocks = contentBlocks.toMutableList().also {
                                                        it[index] = block.copy(scale = localScale)
                                                    }
                                                },
                                                valueRange = 0.3f..1f,
                                                modifier = Modifier.weight(1f).height(24.dp),
                                                colors = SliderDefaults.colors(thumbColor = Primary, activeTrackColor = Primary)
                                            )
                                            Text("${(localScale * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            // 对齐按钮
                                            listOf("START" to Icons.Outlined.FormatAlignLeft, "CENTER" to Icons.Outlined.FormatAlignCenter, "END" to Icons.Outlined.FormatAlignRight).forEach { (align, icon) ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (block.align == align) Primary.copy(alpha = 0.2f) else Color.Transparent)
                                                        .clickable {
                                                            contentBlocks = contentBlocks.toMutableList().also {
                                                                it[index] = block.copy(align = align)
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(imageVector = icon, contentDescription = align, tint = if (block.align == align) Primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ===== 极简单行工具栏 =====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 左侧：文件夹
                        Surface(
                            onClick = { showFolderPicker = !showFolderPicker },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Outlined.Folder, null, tint = Primary, modifier = Modifier.size(12.dp))
                                Text(folder.ifBlank { "默认" }, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        
                        // 中间：评级星星（极小）
                        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                            (1..5).forEach { level ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { rating = if (rating == level) 0 else level },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (level <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (level <= rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        
                        // 右侧：UP按钮
                        val upAlpha = (0.4f + (upCount.coerceAtMost(30) / 30f) * 0.6f).coerceIn(0.4f, 1f)
                        Surface(
                            onClick = { upCount++ },
                            shape = RoundedCornerShape(10.dp),
                            color = Indigo.copy(alpha = upAlpha)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Filled.TrendingUp, null, tint = Color.White, modifier = Modifier.size(10.dp))
                                Text("×$upCount", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }
                    }
                    
                    // 展开的文件夹选择面板
                    AnimatedVisibility(
                        visible = showFolderPicker,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (existingFolders.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        items(existingFolders) { folderName ->
                                            val isSelected = folder == folderName
                                            Surface(
                                                onClick = { 
                                                    folder = folderName
                                                    showFolderPicker = false
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) Primary else MaterialTheme.colorScheme.background
                                            ) {
                                                Text(
                                                    text = folderName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isSelected) OnPrimary else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                if (showNewFolderInput) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BasicTextField(
                                            value = newFolderName,
                                            onValueChange = { newFolderName = it },
                                            textStyle = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface),
                                            cursorBrush = SolidColor(Primary),
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            singleLine = true,
                                            decorationBox = { innerTextField ->
                                                Box {
                                                    if (newFolderName.isEmpty()) {
                                                        Text("新文件夹...", style = TextStyle(fontSize = 12.sp, color = TextTertiaryDark))
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )
                                        IconButton(
                                            onClick = {
                                                if (newFolderName.isNotBlank()) {
                                                    folder = newFolderName.trim()
                                                    newFolderName = ""
                                                    showNewFolderInput = false
                                                    showFolderPicker = false
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Check, null, tint = Primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                } else {
                                    Surface(
                                        onClick = { showNewFolderInput = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier.border(
                                            1.dp,
                                            Primary.copy(0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Outlined.Add, null, tint = Primary, modifier = Modifier.size(12.dp))
                                            Text("新建", style = MaterialTheme.typography.labelSmall, color = Primary)
                                        }
                                    }
                                }
                            }
                        }
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FormatButton(icon = Icons.Outlined.Title)
                                FormatButton(icon = Icons.Outlined.FormatBold)
                                FormatButton(icon = Icons.Outlined.Code)
                                // 图片选择按钮
                                IconButton(
                                    onClick = {
                                        // 图片追加到末尾，LaunchedEffect 会自动在图片后添加空文字块
                                        insertImageAtIndex = contentBlocks.size
                                        imagePickerLauncher.launch("image/*")
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AddPhotoAlternate,
                                        contentDescription = "Add image",
                                        tint = if (contentBlocks.any { it is com.mindstitch.app.data.ContentBlock.Image }) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = "Voice",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && !isSaving) {
                                isSaving = true
                                scope.launch {
                                    if (isEditMode && existingIdea != null) {
                                        repository.update(
                                            existingIdea!!.copy(
                                                title = title.trim(),
                                                contentBlocks = com.mindstitch.app.data.ContentBlock.listToJson(contentBlocks),
                                                folder = folder.trim().ifBlank { "Default" },
                                                type = if (contentBlocks.any { it is com.mindstitch.app.data.ContentBlock.Image }) "IMAGE" else "TEXT",
                                                rating = rating,
                                                upCount = upCount
                                            )
                                        )
                                    } else {
                                        repository.insert(
                                            IdeaEntity(
                                                title = title.trim(),
                                                contentBlocks = com.mindstitch.app.data.ContentBlock.listToJson(contentBlocks),
                                                type = if (contentBlocks.any { it is com.mindstitch.app.data.ContentBlock.Image }) "IMAGE" else "TEXT",
                                                folder = folder.trim().ifBlank { "Default" },
                                                rating = rating,
                                                upCount = upCount
                                            )
                                        )
                                    }
                                    onSaveAndStitch()
                                }
                            }
                        },
                        enabled = title.isNotBlank() && !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) Indigo else Primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isEditMode) Icons.Outlined.SaveAlt else Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                isSaving -> "保存中..."
                                isEditMode -> "保存修改"
                                else -> "Save & Stitch"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.width(128.dp).height(6.dp),
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun FormatButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    IconButton(onClick = { }, modifier = Modifier.size(40.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
