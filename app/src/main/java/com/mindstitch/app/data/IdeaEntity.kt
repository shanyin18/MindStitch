package com.mindstitch.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val contentBlocks: String = "[]",  // JSON 格式的内容块列表
    val type: String = "TEXT",
    val tags: String = "",
    val folder: String = "Default",
    val rating: Int = 0,      // 评级 0-5，0表示未评级
    val upCount: Int = 0,     // UP次数（关注度）
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // 获取内容块列表
    fun getBlocks(): List<ContentBlock> = ContentBlock.listFromJson(contentBlocks)
    
    // 是否包含图片
    fun hasImages(): Boolean = getBlocks().any { it is ContentBlock.Image }
    
    // 获取纯文本内容（用于搜索）
    fun getTextContent(): String = getBlocks()
        .filterIsInstance<ContentBlock.Text>()
        .joinToString("\n") { it.content }
}


