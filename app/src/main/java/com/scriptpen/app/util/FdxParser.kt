package com.scriptpen.app.util

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

/**
 * 解析 Final Draft 的 .fdx 文件。
 * fdx 结构为 <FinalDraft><Content><Paragraph Type="...">文本</Paragraph>...</Content></FinalDraft>
 * 这里只抽取每个 Paragraph 的纯文本，按行拼接（场景标题行因前缀特征可被后续识别）。
 */
object FdxParser {

    fun parse(input: InputStream): String {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(input, "UTF-8")

        val sb = StringBuilder()
        var event = parser.eventType
        var currentText = StringBuilder()
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    if (parser.name.equals("Paragraph", ignoreCase = true)) {
                        currentText = StringBuilder()
                    }
                }
                XmlPullParser.TEXT -> {
                    currentText.append(parser.text)
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("Paragraph", ignoreCase = true)) {
                        val line = currentText.toString().trim()
                        if (line.isNotEmpty()) {
                            sb.append(line).append("\n")
                        }
                    }
                }
            }
            event = parser.next()
        }
        return sb.toString().trim()
    }
}
