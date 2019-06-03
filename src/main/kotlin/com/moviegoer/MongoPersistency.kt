package com.moviegoer

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

object MongoPersistency {

    private lateinit var mongodb: MongoDatabase
    private lateinit var colBrief: MongoCollection<Document>

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
        colBrief.insertMany(list)
    }

}