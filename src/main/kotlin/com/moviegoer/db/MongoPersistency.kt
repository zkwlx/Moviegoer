package com.moviegoer.db

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import com.moviegoer.utils.Log
import org.bson.Document

object MongoPersistency {

    private lateinit var mongodb: MongoDatabase
    private lateinit var colBrief: MongoCollection<Document>
    private lateinit var colDetail: MongoCollection<Document>

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

    fun insertDetail(detail: Document) {
        synchronized(this) {
            colDetail.insertOne(detail)
        }
    }

    private val briefIterator: MongoCursor<Document> by lazy {
        colBrief.find().projection(Document("url", 1).append("_id", 0)).iterator()
    }

    fun nextBrief(): Document? {
        synchronized(this){
            return briefIterator.tryNext()
        }
    }

}