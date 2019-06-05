package com.moviegoer

import com.google.gson.JsonParser
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.moviegoer.utils.Log
import org.bson.Document

object MongoPersistency {

    private lateinit var mongodb: MongoDatabase
    private lateinit var colBrief: MongoCollection<Document>
    private var jsonParser: JsonParser = JsonParser()

    init {
        try {
            // 连接到 mongodb 服务
            val mongoClient = MongoClients.create()
            // 连接到数据库
            mongodb = mongoClient.getDatabase("movie")
            println("Connect to database successfully")
            colBrief = mongodb.getCollection("test")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertMany(list: MutableList<out Document>) {
        if (list.isNotEmpty()) {
            colBrief.insertMany(list)
        }
    }

    fun parseToDocumentList(content: String): MutableList<out Document>? {
        // 将 json 解析成 document list
        val element = jsonParser.parse(content)
        if (!element.isJsonObject) {
            return null
        }
        val data = element.asJsonObject["data"]
        val docList = mutableListOf<Document>()
        data.asJsonArray.forEach { item ->
            docList.add(Document.parse(item.toString()))
        }
        return docList
    }

}