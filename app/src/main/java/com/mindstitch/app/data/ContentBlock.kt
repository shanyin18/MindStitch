package com.mindstitch.app.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * 内容块 - 可以是文字或图片
 */
sealed class ContentBlock {
    abstract fun toJson(): JSONObject
    
    data class Text(val content: String) : ContentBlock() {
        override fun toJson(): JSONObject = JSONObject().apply {
            put("type", "text")
            put("content", content)
        }
    }
    
    data class Image(
        val uri: String,
        val scale: Float = 1f,
        val align: String = "START"  // START / CENTER / END
    ) : ContentBlock() {
        override fun toJson(): JSONObject = JSONObject().apply {
            put("type", "image")
            put("uri", uri)
            put("scale", scale)
            put("align", align)
        }
    }
    
    companion object {
        fun fromJson(json: JSONObject): ContentBlock? {
            return when (json.optString("type")) {
                "text" -> Text(json.optString("content", ""))
                "image" -> Image(
                    uri = json.optString("uri", ""),
                    scale = json.optDouble("scale", 1.0).toFloat(),
                    align = json.optString("align", "START")
                )
                else -> null
            }
        }
        
        fun listToJson(blocks: List<ContentBlock>): String {
            val arr = JSONArray()
            blocks.forEach { arr.put(it.toJson()) }
            return arr.toString()
        }
        
        fun listFromJson(json: String): List<ContentBlock> {
            if (json.isBlank()) return emptyList()
            return try {
                val arr = JSONArray(json)
                (0 until arr.length()).mapNotNull { fromJson(arr.getJSONObject(it)) }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
