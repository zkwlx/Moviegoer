package com.moviegoer

import com.mongodb.client.MongoClients
import org.bson.Document
import java.util.*

fun main() {
    val crawler = MovieCrawler()
    crawler.start()

    testMongoDb()
}

fun testMongoDb() {

    val doc = Document("name", "MongoDB")
        .append("type", "database")
        .append("count", 1)
        .append("versions", Arrays.asList<String>("v3.2", "v3.0", "v2.6"))
        .append("info", Document("x", 203).append("y", 102))
    try {
        // 连接到 mongodb 服务
        val mongoClient = MongoClients.create()

        // 连接到数据库
        val mongoDatabase = mongoClient.getDatabase("movie")
        println("Connect to database successfully")
        val colle = mongoDatabase.getCollection("test")
        colle.insertOne(doc)
    } catch (e: Exception) {
        System.err.println(e.javaClass.name + ": " + e.message)
    }


}

fun testSqlite() {
    //    var c: Connection? = null
//    try {
//        Class.forName("org.sqlite.JDBC")
//        c = DriverManager.getConnection("jdbc:sqlite:test.db")
//    } catch (e: Exception) {
//        System.err.println(e.javaClass.name + ": " + e.message)
//        System.exit(0)
//    }
}