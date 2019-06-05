package com.moviegoer

import com.moviegoer.utils.Log
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.bson.Document

class MovieCrawler {

    fun start() {
        val builder = OkHttpClient.Builder()
        val client = builder.build()

        runBlocking {
            var offset = 0
            while (true) {
                val url = UrlProvider.next(offset)
                val element = HttpRequester.requestBrief(url)
                if (element == null) {
                    Log.e("请求失败： $url")
                    break
                }
                val data = element.asJsonObject["data"]
                val docList = mutableListOf<Document>()
                data.asJsonArray.forEach { item ->
                    docList.add(Document.parse(item.toString()))
                }
            }
        }
    }
}