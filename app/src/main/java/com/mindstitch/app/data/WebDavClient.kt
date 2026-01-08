package com.mindstitch.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebDavClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 检查连接（尝试列出根目录）
    suspend fun checkConnection(url: String, user: String, pass: String): Boolean {
        return withContext(Dispatchers.IO) {
            val credential = Credentials.basic(user, pass)
            
            // 确保URL以 / 结尾
            val validUrl = if (url.endsWith("/")) url else "$url/"
            
            val request = Request.Builder()
                .url(validUrl)
                .header("Authorization", credential)
                .method("PROPFIND", ByteArray(0).toRequestBody(null))
                .header("Depth", "0")
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    response.isSuccessful || response.code == 207 // 207 Multi-Status is typical for WebDAV
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // 上传文件 (文本内容)
    suspend fun uploadFile(url: String, user: String, pass: String, fileName: String, content: String): Result<Boolean> {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return uploadFileInternal(url, user, pass, fileName, content.toByteArray(), mediaType)
    }

    // 上传文件 (二进制数据)
    suspend fun uploadFile(url: String, user: String, pass: String, fileName: String, data: ByteArray, mimeType: String = "application/octet-stream"): Result<Boolean> {
        val mediaType = mimeType.toMediaType()
        return uploadFileInternal(url, user, pass, fileName, data, mediaType)
    }

    private suspend fun uploadFileInternal(url: String, user: String, pass: String, fileName: String, data: ByteArray, mediaType: okhttp3.MediaType): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            val credential = Credentials.basic(user, pass)
            
            val baseUrl = if (url.endsWith("/")) url else "$url/"
            val fileUrl = baseUrl + fileName
            
            val request = Request.Builder()
                .url(fileUrl)
                .header("Authorization", credential)
                .put(data.toRequestBody(mediaType))
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful || response.code == 201 || response.code == 204) {
                        Result.success(true)
                    } else {
                        val errorBody = response.body?.string() ?: ""
                        Result.failure(Exception("上传失败: HTTP ${response.code} $errorBody"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // 下载文件 (返回字符串)
    suspend fun downloadFile(url: String, user: String, pass: String, fileName: String): String? {
        val bytes = downloadFileBytes(url, user, pass, fileName) ?: return null
        return String(bytes)
    }

    // 下载文件 (返回字节数组)
    suspend fun downloadFileBytes(url: String, user: String, pass: String, fileName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val credential = Credentials.basic(user, pass)
            
            val baseUrl = if (url.endsWith("/")) url else "$url/"
            val fileUrl = baseUrl + fileName
            
            val request = Request.Builder()
                .url(fileUrl)
                .header("Authorization", credential)
                .get()
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.bytes()
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // 创建文件夹
    suspend fun createFolder(url: String, user: String, pass: String, folderName: String): Boolean {
        // ... (keep existing implementation)
        return withContext(Dispatchers.IO) {
            val credential = Credentials.basic(user, pass)
            
            val baseUrl = if (url.endsWith("/")) url else "$url/"
            val folderUrl = baseUrl + folderName
            
            val request = Request.Builder()
                .url(folderUrl)
                .header("Authorization", credential)
                .method("MKCOL", null)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    response.code == 201 || response.code == 405 || response.isSuccessful
                }
            } catch (e: Exception) {
                false 
            }
        }
    }
}
