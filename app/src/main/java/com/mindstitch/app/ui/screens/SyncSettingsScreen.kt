package com.mindstitch.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mindstitch.app.data.SyncManager
import com.mindstitch.app.ui.theme.Primary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
    val syncManager = remember { SyncManager.getInstance(context) }
    
    // SharedPreferences
    val prefs = remember { context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE) }
    
    var serverUrl by remember { mutableStateOf(prefs.getString("server_url", "https://dav.jianguoyun.com/dav/") ?: "") }
    var username by remember { mutableStateOf(prefs.getString("username", "") ?: "") }
    var password by remember { mutableStateOf(prefs.getString("password", "") ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var isTestingConnection by remember { mutableStateOf(false) }
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    
    // 保存配置
    fun saveConfig() {
        prefs.edit()
            .putString("server_url", serverUrl)
            .putString("username", username)
            .putString("password", password)
            .apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("云同步设置 (WebDAV)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "配置坚果云 WebDAV",
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary
                    )
                    Text(
                        "请在坚果云网页版 -> 账户信息 -> 安全选项 -> 第三方应用管理 中创建应用密码。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("服务器地址") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("坚果云账号 (邮箱)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("应用密码") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    "Toggle password visibility"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    
                    Button(
                        onClick = {
                            saveConfig()
                            isTestingConnection = true
                            scope.launch {
                                val success = syncManager.testConnection(serverUrl, username, password)
                                isTestingConnection = false
                                if (success) {
                                    Toast.makeText(context, "连接成功！", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "连接失败，请检查配置", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isTestingConnection
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("正在测试...")
                        } else {
                            Icon(Icons.Default.Check, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("保存并测试连接")
                        }
                    }
                }
            }
            
            HorizontalDivider()
            
            Text(
                "同步操作",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // 备份按钮
            Button(
                onClick = {
                    saveConfig()
                    isBackingUp = true
                    scope.launch {
                        val result = syncManager.performBackup(serverUrl, username, password)
                        isBackingUp = false
                        result.onSuccess {
                            Toast.makeText(context, "备份成功！", Toast.LENGTH_SHORT).show()
                        }.onFailure { e ->
                            Toast.makeText(context, "备份失败: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBackingUp && !isRestoring && !isTestingConnection,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (isBackingUp) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("正在备份...")
                } else {
                    Icon(Icons.Default.CloudUpload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("立即备份到云端")
                }
            }
            
            // 恢复按钮
            OutlinedButton(
                onClick = {
                    saveConfig()
                    isRestoring = true
                    scope.launch {
                        val result = syncManager.performRestore(serverUrl, username, password)
                        isRestoring = false
                        result.onSuccess {
                            Toast.makeText(context, "恢复成功！", Toast.LENGTH_SHORT).show()
                        }.onFailure { e ->
                            Toast.makeText(context, "恢复失败: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBackingUp && !isRestoring && !isTestingConnection
            ) {
                if (isRestoring) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("正在恢复...")
                } else {
                    Icon(Icons.Default.CloudDownload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("从云端恢复数据")
                }
            }
            
            Text(
                "注意：恢复数据会将云端备份合并到本地。如果本地已有相同数据，可能会更新或创建副本。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
