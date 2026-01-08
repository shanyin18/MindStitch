package com.mindstitch.app.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class BackupData(
    val ideas: List<IdeaEntity>,
    val todos: List<TodoEntity>,
    val version: Int = 1,
    val backupTime: Long = System.currentTimeMillis()
)

class SyncManager(private val context: Context) {
    private val ideaDao = AppDatabase.getDatabase(context).ideaDao()
    private val todoDao = AppDatabase.getDatabase(context).todoDao()
    private val webDavClient = WebDavClient()
    private val gson = Gson()
    
    private val BACKUP_FILENAME = "MindStitchBackup.json"
    private val BACKUP_FOLDER = "MindStitch"
    private val IMAGES_FOLDER = "$BACKUP_FOLDER/images"

    // 执行备份
    suspend fun performBackup(url: String, user: String, pass: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 获取所有数据
                val allIdeas = ideaDao.getIdeasSince(0)
                val allTodos = todoDao.getAllTodos()
                
                if (allIdeas.isEmpty() && allTodos.isEmpty()) {
                    return@withContext Result.failure(Exception("没有数据可备份"))
                }

                // 2. 尝试创建文件夹
                webDavClient.createFolder(url, user, pass, BACKUP_FOLDER)
                webDavClient.createFolder(url, user, pass, IMAGES_FOLDER)
                
                // 3. 处理图片备份
                val updatedIdeas = allIdeas.map { idea ->
                    val blocks = idea.getBlocks().map { block ->
                        if (block is ContentBlock.Image) {
                            val localUri = android.net.Uri.parse(block.uri)
                            // 生成云端文件名 (使用hash或简单规则，这里用时间戳+hash避免冲突)
                            val fileName = "img_${idea.id}_${localUri.lastPathSegment ?: "unknown"}"
                            
                            // 读取文件并上传
                            try {
                                val inputStream = context.contentResolver.openInputStream(localUri)
                                if (inputStream != null) {
                                    val bytes = inputStream.readBytes()
                                    inputStream.close()
                                    
                                    val uploadResult = webDavClient.uploadFile(
                                        url, user, pass, 
                                        "$IMAGES_FOLDER/$fileName", 
                                        bytes
                                    )
                                    // 如果上传失败，我们暂时忽略，保留原URI或者标记错误？这里选择继续，但修改URI指向备份文件名以便恢复
                                    if (uploadResult.isSuccess) {
                                        // 修改block中的URI为备份文件名，以便恢复时使用
                                        block.copy(uri = "backup://$fileName") // 使用特殊schema标识
                                    } else {
                                        block
                                    }
                                } else {
                                    block
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                block // 读取失败保持原样
                            }
                        } else {
                            block
                        }
                    }
                    // 更新idea的contentBlocks JSON
                    idea.copy(contentBlocks = ContentBlock.listToJson(blocks))
                }
                
                // 4. 创建备份对象 (使用更新了URI的ideas)
                val backupData = BackupData(updatedIdeas, allTodos)
                
                // 5. 转换为JSON
                val json = gson.toJson(backupData)
                
                // 6. 上传JSON
                val targetPath = "$BACKUP_FOLDER/$BACKUP_FILENAME"
                val uploadResult = webDavClient.uploadFile(url, user, pass, targetPath, json)
                
                uploadResult.getOrThrow()
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 执行恢复
    suspend fun performRestore(url: String, user: String, pass: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 下载JSON
                val targetPath = "$BACKUP_FOLDER/$BACKUP_FILENAME"
                val json = webDavClient.downloadFile(url, user, pass, targetPath)
                    ?: return@withContext Result.failure(Exception("未找到备份文件或下载失败"))
                
                // 2. 解析JSON
                val backupData = gson.fromJson(json, BackupData::class.java)
                
                // 3. 恢复数据
                val restoredIdeas = backupData.ideas.map { idea ->
                    val blocks = idea.getBlocks().map { block ->
                        if (block is ContentBlock.Image && block.uri.startsWith("backup://")) {
                            val fileName = block.uri.substringAfter("backup://")
                            // 检查本地是否已存在该文件 (可选优化)
                            val localFile = java.io.File(context.filesDir, "restored_images/$fileName")
                            
                            if (!localFile.exists()) {
                                // 下载图片
                                val imageBytes = webDavClient.downloadFileBytes(url, user, pass, "$IMAGES_FOLDER/$fileName")
                                if (imageBytes != null) {
                                    localFile.parentFile?.mkdirs()
                                    localFile.writeBytes(imageBytes)
                                }
                            }
                            
                            // 更新URI为本地路径
                            if (localFile.exists()) {
                                block.copy(uri = android.net.Uri.fromFile(localFile).toString())
                            } else {
                                block // 下载失败，保持原样或标记错误
                            }
                        } else {
                            block
                        }
                    }
                    idea.copy(contentBlocks = ContentBlock.listToJson(blocks))
                }

                // 4. 写入数据库
                var restoredCount = 0
                restoredIdeas.forEach { idea ->
                    ideaDao.insert(idea)
                    restoredCount++
                }
                backupData.todos.forEach { todo ->
                    todoDao.insert(todo)
                    restoredCount++
                }
                
                Result.success(true)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 测试连接
    suspend fun testConnection(url: String, user: String, pass: String): Boolean {
        return webDavClient.checkConnection(url, user, pass)
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(context: Context): SyncManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SyncManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
