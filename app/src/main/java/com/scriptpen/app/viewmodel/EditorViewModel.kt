package com.scriptpen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.scriptpen.app.data.AppDatabase
import com.scriptpen.app.data.ScriptDocument
import com.scriptpen.app.util.ScriptFormatter
import com.scriptpen.app.util.WordCounter
import com.scriptpen.app.util.WordStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditorViewModel(
    private val database: AppDatabase,
    private val documentId: Long
) : ViewModel() {

    private val dao = database.scriptDao()
    private var loaded: ScriptDocument? = null

    val content = MutableStateFlow("")
    val title = MutableStateFlow("")
    val background = MutableStateFlow(0xFF1E1E1E.toInt())
    val fontSize = MutableStateFlow(16f)

    val stats: StateFlow<WordStats> = content
        .debounce(200)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WordCounter.count("")
        )

    init {
        viewModelScope.launch {
            val doc = dao.getById(documentId)
            if (doc != null) {
                loaded = doc
                content.value = doc.content
                title.value = doc.title
                background.value = doc.background
                fontSize.value = doc.fontSize
            }
        }
    }

    fun onContentChange(new: String) {
        content.value = new
        scheduleSave()
    }

    fun setTitle(new: String) {
        title.value = new
        scheduleSave()
    }

    fun setBackground(color: Int) {
        background.value = color
        scheduleSave()
    }

    fun setFontSize(size: Float) {
        fontSize.value = size
        scheduleSave()
    }

    fun applyAutoFormat() {
        content.value = ScriptFormatter.autoFormat(content.value)
        scheduleSave()
    }

    fun importContent(text: String) {
        content.value = text
        scheduleSave()
    }

    /** 单条替换（替换第一个匹配，返回是否成功） */
    fun replaceNext(from: String, to: String): Boolean {
        if (from.isEmpty()) return false
        val idx = content.value.indexOf(from)
        if (idx < 0) return false
        content.value = content.value.replaceRange(idx, idx + from.length, to)
        scheduleSave()
        return true
    }

    /** 批量替换（全部替换） */
    fun replaceAll(from: String, to: String): Int {
        if (from.isEmpty()) return 0
        val before = content.value
        val after = before.replace(from, to)
        content.value = after
        val count = (before.length - after.length) / (before.length - from.length).coerceAtLeast(1)
        val n = countOccurrences(before, from)
        scheduleSave()
        return n
    }

    private fun countOccurrences(text: String, sub: String): Int {
        if (sub.isEmpty()) return 0
        var count = 0
        var idx = text.indexOf(sub)
        while (idx >= 0) {
            count++
            idx = text.indexOf(sub, idx + sub.length)
        }
        return count
    }

    private var saveJob: Job? = null
    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(800)
            persist()
        }
    }

    private suspend fun persist() {
        val doc = loaded ?: return
        dao.update(
            doc.copy(
                title = title.value,
                content = content.value,
                background = background.value,
                fontSize = fontSize.value,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun saveNow() {
        viewModelScope.launch { persist() }
    }
}

class EditorViewModelFactory(
    private val database: AppDatabase,
    private val documentId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditorViewModel(database, documentId) as T
    }
}
