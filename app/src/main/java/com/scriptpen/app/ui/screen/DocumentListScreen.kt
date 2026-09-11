package com.scriptpen.app.ui.screen

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.scriptpen.app.ScriptPenApplication
import com.scriptpen.app.data.ScriptDocument
import com.scriptpen.app.viewmodel.DocumentListViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    navController: NavHostController,
    application: Application
) {
    val db = (application as ScriptPenApplication).database
    val viewModel: DocumentListViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                DocumentListViewModel(db) as T
        }
    )
    val documents by viewModel.documents.collectAsState()
    val scope = rememberCoroutineScope()

    var showDeleteId by remember { mutableStateOf<Long?>(null) }
    var renameTarget by remember { mutableStateOf<ScriptDocument?>(null) }
    var renameText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("剧本笔记") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    val id = viewModel.createDocument("未命名剧本", 0xFF1E1E1E.toInt(), 16f)
                    navController.navigate("editor/$id")
                }
            }) { Icon(Icons.Filled.Add, "新建") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(documents, key = { it.id }) { doc ->
                DocumentRow(
                    doc = doc,
                    onClick = { navController.navigate("editor/${doc.id}") },
                    onRename = { renameTarget = doc; renameText = doc.title },
                    onDelete = { showDeleteId = doc.id }
                )
            }
            if (documents.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("还没有剧本", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("点击右下角 + 开始创作", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    showDeleteId?.let { id ->
        val doc = documents.find { it.id == id }
        AlertDialog(
            onDismissRequest = { showDeleteId = null },
            title = { Text("删除确认") },
            text = { Text("确定删除「${doc?.title ?: ""}」？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    doc?.let { viewModel.delete(it) }
                    showDeleteId = null
                }) { Text("删除") }
            },
            dismissButton = { TextButton(onClick = { showDeleteId = null }) { Text("取消") } }
        )
    }

    renameTarget?.let { doc ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("重命名") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text("标题") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.rename(doc.id, renameText.ifBlank { "未命名剧本" })
                    renameTarget = null
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { renameTarget = null }) { Text("取消") } }
        )
    }
}

@Composable
private fun DocumentRow(
    doc: ScriptDocument,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${formatTime(doc.updatedAt)} · ${doc.content.length} 字",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRename) { Icon(Icons.Filled.Edit, "重命名") }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "删除") }
        }
    }
}

private fun formatTime(ts: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(ts))
}
