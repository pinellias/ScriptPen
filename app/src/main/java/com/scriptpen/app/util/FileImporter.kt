package com.scriptpen.app.util

import android.content.Context
import android.net.Uri

/**
 * 从系统文件选择器返回的 Uri 读取文本。
 * 支持：txt / md（纯文本读取）、fdx（Final Draft XML 解析）。
 */
object FileImporter {

    fun readText(context: Context, uri: Uri): String {
        val name = uri.lastPathSegment ?: ""
        context.contentResolver.openInputStream(uri)?.use { stream ->
            return if (name.endsWith(".fdx", ignoreCase = true)) {
                FdxParser.parse(stream)
            } else {
                stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }
        }
        return ""
    }
}
