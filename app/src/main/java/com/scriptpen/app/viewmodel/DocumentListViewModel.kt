package com.scriptpen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scriptpen.app.data.AppDatabase
import com.scriptpen.app.data.ScriptDocument
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentListViewModel(private val database: AppDatabase) : ViewModel() {

    val documents: StateFlow<List<ScriptDocument>> = database.scriptDao().observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun delete(doc: ScriptDocument) = viewModelScope.launch {
        database.scriptDao().delete(doc)
    }

    suspend fun createDocument(title: String, background: Int, fontSize: Float): Long {
        val doc = ScriptDocument(
            title = title,
            content = "",
            background = background,
            fontSize = fontSize
        )
        return database.scriptDao().insert(doc)
    }

    suspend fun rename(id: Long, newTitle: String) {
        val doc = database.scriptDao().getById(id) ?: return
        database.scriptDao().update(
            doc.copy(title = newTitle, updatedAt = System.currentTimeMillis())
        )
    }
}
