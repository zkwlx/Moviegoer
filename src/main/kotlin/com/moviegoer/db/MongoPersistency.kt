package com.moviegoer.db

import com.mongodb.client.*
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.*
import com.moviegoer.utils.Log
import org.bson.Document
import org.bson.conversions.Bson

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
        //TODO rate 这里是临时修改，只获取没有评分的电影
        colBrief.find(eq("rate", ""))
            .noCursorTimeout(true)
            .projection(Document("url", 1).append("_id", 0))
            .iterator()
    }

    fun isExist(urlPath: String): Boolean {
        val filter = Document("url", urlPath)
        val count = colDetail.find(filter).count()
        return count > 0
    }

    fun findDetail(urlPath: String): FindIterable<Document> {
        val filter = Document("url", urlPath)
        return colDetail.find(filter)
    }

    fun setAnyDetail(urlPath: String, year: Pair<String, Int>?, countrys: Pair<String, List<String>>?) {
        val filter = Document("url", urlPath)
        val bsons = arrayListOf<Bson>()
        if (year != null) {
            bsons.add(set(year.first, year.second))
        }
        if (countrys != null) {
            bsons.add(pushEach(countrys.first, countrys.second))
        }
        if (bsons.isNotEmpty()) {
            colDetail.updateMany(filter, combine(bsons))
        }
    }

    fun nextBrief(): Document? {
        synchronized(this) {
            return briefIterator.tryNext()
        }
    }

}