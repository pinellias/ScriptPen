package com.scriptpen.app.util

/**
 * 轻量剧本排版与结构识别。
 * 不做完整的剧本格式引擎，只做：
 *  - 场景标题（Scene Heading）识别，用于统计与后续导出加粗
 *  - 自动排版：清理行尾空白、压缩多余空行
 */
object ScriptFormatter {

    // 常见中文地点/时间提示词，用于辅助识别中文场景标题
    private val LOCATION_HINTS = listOf(
        "客厅", "房间", "卧室", "厨房", "办公室", "酒吧", "医院", "学校", "公园",
        "咖啡", "街道", "室外", "室内", "山顶", "海边", "车站", "机场", "夜", "日",
        "清晨", "黄昏", "车内", "厂房", "餐厅", "教室", "牢房", "广场", "巷口"
    )

    /** 判断某一行是否为场景标题（轻量启发式）。 */
    fun isSceneHeading(line: String): Boolean {
        val t = line.trim()
        if (t.isEmpty()) return false
        val upper = t.uppercase()
        // 1) 以 INT. / EXT. / INT/EXT 等剧本标准前缀开头
        if (upper.startsWith("INT.") || upper.startsWith("INT ") ||
            upper.startsWith("EXT.") || upper.startsWith("EXT ") ||
            upper.startsWith("INT/EXT") || upper.startsWith("INT./EXT")
        ) return true
        // 2) 全大写英文行（含空格、长度适中），多半是场景/转场标题
        if (t == upper && t.any { it.isLetter() } && t.length in 4..40 && t.contains(' ')) return true
        // 3) 含地点/时间提示词且较短，视为中文场景标题
        if (LOCATION_HINTS.any { t.contains(it) } && t.length <= 30) return true
        return false
    }

    fun countSceneHeadings(text: String): Int {
        if (text.isEmpty()) return 0
        return text.lineSequence().count { isSceneHeading(it) }
    }

    /** 轻量自动排版：去行尾空白、至多保留一个空行、去掉首尾空行。 */
    fun autoFormat(text: String): String {
        val lines = text.split("\n").map { it.trimEnd() }
        val result = mutableListOf<String>()
        var blankCount = 0
        for (line in lines) {
            if (line.isBlank()) {
                blankCount++
                if (blankCount <= 1) result.add("")
            } else {
                blankCount = 0
                result.add(line)
            }
        }
        while (result.isNotEmpty() && result.first().isBlank()) result.removeAt(0)
        while (result.isNotEmpty() && result.last().isBlank()) result.removeAt(result.lastIndex)
        return result.joinToString("\n")
    }
}
