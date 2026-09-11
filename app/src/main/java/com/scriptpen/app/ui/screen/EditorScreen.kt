package com.scriptpen.app.ui.screen

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.scriptpen.app.ScriptPenApplication
import com.scriptpen.app.ui.component.SearchReplaceBar
import com.scriptpen.app.ui.component.WordCountBar
import com.scriptpen.app.util.FileImporter
import com.scriptpen.app.viewmodel.EditorViewModel
import com.scriptpen.app.viewmodel.EditorViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    navController: NavHostController,
    documentId: Long,
    application: Application
) {
    val db = (application as ScriptPenApplication).database
    val viewModel: EditorViewModel = viewModel(factory = EditorViewModelFactory(db, documentId))

    val title by viewModel.title.collectAsState()
    val background by viewModel.background.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val content by viewModel.content.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var replacement by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(false) }

    var tfv by remember { mutableStateOf(TextFieldValue(content)) }
    LaunchedEffect(content) {
        if (tfv.text != content) tfv = tfv.copy(text = content)
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importContent(FileImporter.readText(application, it)) }
    }

    fun findNext() {
        if (query.isEmpty()) return
        val text = tfv.text
        val from = tfv.selection.end
        var idx = text.indexOf(query, from)
        if (idx < 0) idx = text.indexOf(query)
        if (idx >= 0) tfv = tfv.copy(selection = TextRange(idx, idx + query.length))
    }

    fun findPrev() {
        if (query.isEmpty()) return
        val text = tfv.text
        val from = (tfv.selection.start - 1).coerceAtLeast(0)
        var idx = text.lastIndexOf(query, from)
        if (idx < 0) idx = text.lastIndexOf(query)
        if (idx >= 0) tfv = tfv.copy(selection = TextRange(idx, idx + query.length))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title.ifBlank { "未命名剧本" }, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Filled.Search, "搜索")
                    }
                    IconButton(onClick = { viewModel.applyAutoFormat() }) {
                        Icon(Icons.Filled.AutoAwesome, "自动排版")
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Palette, "外观")
                    }
                    IconButton(onClick = {
                        importLauncher.launch(
                            arrayOf("text/plain", "text/markdown", "application/xml", "*/*")
                        )
                    }) {
                        Icon(Icons.Filled.FileOpen, "导入")
                    }
                }
            )
        },
        bottomBar = { WordCountBar(stats) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (showSearch) {
                SearchReplaceBar(
                    query = query,
                    replacement = replacement,
                    onQueryChange = { query = it },
                    onReplacementChange = { replacement = it },
                    onFindPrev = { findPrev() },
                    onFindNext = { findNext() },
                    onReplaceNext = { viewModel.replaceNext(query, replacement) },
                    onReplaceAll = { viewModel.replaceAll(query, replacement) },
                    onClose = { showSearch = false }
                )
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(background))
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = tfv,
                    onValueChange = {
                        tfv = it
                        viewModel.onContentChange(it.text)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    textStyle = TextStyle(
                        color = if (isLightColor(background)) Color.Black else Color.White,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.5f).sp
                    ),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxSize()) {
                            if (tfv.text.isEmpty()) {
                                Text("开始创作…", color = Color.Gray)
                            }
                            inner()
                        }
                    }
                )
            }
        }
    }

    if (showSettings) {
        SettingsSheet(
            currentBackground = background,
            currentFontSize = fontSize,
            onBackgroundChange = viewModel::setBackground,
            onFontSizeChange = viewModel::setFontSize,
            onDismiss = { showSettings = false }
        )
    }
}

/** 判断背景色是否为浅色，以决定编辑器文字用黑还是白。 */
fun isLightColor(colorInt: Int): Boolean {
    val r = (colorInt shr 16) and 0xFF
    val g = (colorInt shr 8) and 0xFF
    val b = colorInt and 0xFF
    val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
    return luminance > 0.6
}
