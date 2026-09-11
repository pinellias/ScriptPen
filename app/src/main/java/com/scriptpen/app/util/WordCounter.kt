package com.scriptpen.app.util

/**
 * 统计剧本文本的各项指标。
 */
data class WordStats(
    val totalChars: Int,         // 含空白的总字符数
    val totalCharsNoSpace: Int,  // 去空白后的字符数
    val chineseChars: Int,       // 中文字数
    val englishWords: Int,       // 英文单词数（连续英文字母/数字计为一个）
    val lines: Int,              // 行数
    val paragraphs: Int,         // 段落数（以空行分隔的非空块）
    val scenes: Int              // 识别到的场景标题数
)

object WordCounter {

    fun count(text: String): WordStats {
        if (text.isEmpty()) {
            return WordStats(0, 0, 0, 0, 0, 0, 0)
        }
        val totalChars = text.length
        val noSpace = text.replace("\\s".toRegex(), "").length
        val chinese = text.count { it in '\u4e00'..'\u9fff' }
        val englishWords = Regex("[A-Za-z0-9]+").findAll(text).count()
        val lineCount = text.count { it == '\n' } + 1
        val paragraphs = text.split("\n\\s*\n".toRegex())
            .count { it.trim().isNotEmpty() }
        val scenes = ScriptFormatter.countSceneHeadings(text)
        return WordStats(totalChars, noSpace, chinese, englishWords, lineCount, paragraphs, scenes)
    }
}
