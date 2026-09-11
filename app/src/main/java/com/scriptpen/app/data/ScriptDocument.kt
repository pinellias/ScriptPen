package com.scriptpen.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 一篇剧本 / 笔记文档。
 * background 存 ARGB 颜色值，fontSize 为整篇字号（sp）。
 */
@Entity(tableName = "scripts")
data class ScriptDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val background: Int,
    val fontSize: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
