package com.moviegoer.db

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.moviegoer.utils.Log
import org.bson.Document
import org.jsoup.Jsoup

object MongoPersistency {

    private lateinit var mongodb: MongoDatabase
    private lateinit var colBrief: MongoCollection<Document>
    private lateinit var colDetail: MongoCollection<Document>
    private var jsonParser: JsonParser = JsonParser()

    init {
        try {
            // 连接到 mongodb 服务
            val mongoClient = MongoClients.create()
            // 连接到数据库
            mongodb = mongoClient.getDatabase("movie")
            Log.i("Connect to database successfully")
            colBrief = mongodb.getCollection("all")
            colDetail = mongodb.getCollection("detail")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertManyBriefs(list: MutableList<out Document>) {
        if (list.isNotEmpty()) {
            colBrief.insertMany(list)
        }
    }

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

    fun insertDetail(detail: Document) {
        colDetail.insertOne(detail)
    }

    fun parseToDetail(content: String): Document? {
        var started = false
        val jsonString = StringBuilder()
        content.lineSequence().forEach { line ->
            if (started) {
                if (line == "</script>")
                    return@forEach
                else
                    jsonString.append(line)
            } else if (line == "<script type=\"application/ld+json\">") {
                started = true
            }
        }
        return null
    }

    private val briefIterator: MongoCursor<Document> by lazy {
        colBrief.find().projection(Document("url", 1).append("_id", 0)).iterator()
    }

    fun nextBrief(): Document? {
        return briefIterator.tryNext()
    }

}