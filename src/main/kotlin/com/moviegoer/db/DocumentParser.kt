package com.moviegoer.db

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.moviegoer.utils.Log
import org.bson.Document

class DocumentParser {

    val ERROR_MSG = "#error_msg#"

    private var jsonParser: JsonParser = JsonParser()

    fun parseToBriefsList(content: String): MutableList<out Document>? {
        // 将 json 解析成 document list
        var element: JsonElement? = null
        try {
            element = jsonParser.parse(content)
        } catch (e: Exception) {
            Log.e("parse error $e:${e.message}")
        }
        if (element == null || !element.isJsonObject) {
            return null
        }
        val data = element.asJsonObject["data"]
        val docList = mutableListOf<Document>()
        data.asJsonArray.forEach { item ->
            docList.add(Document.parse(item.toString()))
        }
        return docList
    }


    fun parseToDetail(content: String): Document {
        var started = false
        val jsonString = StringBuilder()
        var document = Document()
        content.lineSequence().forEach { line ->
            if (started) {
                if (line == "</script>")
                    return@forEach
                else
                    jsonString.append(line)
            } else if (line == "<script type=\"application/ld+json\">") {
                started = true
            } else if (line.contains("页面不存在")) {
                document[ERROR_MSG] = "页面不存在"
            }
        }
        if (document.isNotEmpty()) {
            return document
        }
        val finalJson = jsonString.toString()
        try {
            document = Document.parse(finalJson)
            document.remove("@context")
            val duration = document["duration"] as String
            document["duration"] = parseToMinute(duration)
        } catch (e: Exception) {
            Log.e("------error json----\n$finalJson")
            e.printStackTrace()
            document[ERROR_MSG] = "Json 解析失败"
        }
        return document
    }

    private val digitRegex = Regex("[0-9]+")

    /**
     * 将 PT1H30M 格式的时长解析成数字，单位为分钟，
     * 上面例子将解析成 90 分钟
     */
    private fun parseToMinute(durationStr: String): Int {
        if (durationStr.isEmpty()) {
            return -1
        }
        val match = digitRegex.findAll(durationStr)
        val hour = match.elementAt(0).value.toInt()
        val minute = match.elementAt(1).value.toInt()
        return hour * 60 + minute
    }

}