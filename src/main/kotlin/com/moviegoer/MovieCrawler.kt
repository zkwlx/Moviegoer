package com.moviegoer

import com.demo.Utils
import com.google.gson.JsonParser
import com.moviegoer.UrlProvider.walkCountryAndRank
import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.bson.Document
import java.io.IOException
import java.lang.Exception
import kotlin.random.Random

class MovieCrawler {

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun requester(client: OkHttpClient, url: String): Int {
        client.proxySelector()
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", AgentProvider.next())
            .addHeader("Cookie", "bid=${Utils.randomStr(11)};")
            .method("GET", null)
            .build()
        val call = client.newCall(request)
        val response: Response
        //TODO 增加重试功能
        try {
            response = call.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            return ERROR_REQUEST
        }
        var listLength = 0
        response.use {
            val parser = JsonParser()
            val content = it.body()?.string()
            Log.i("------------$content")
            val docList = mutableListOf<Document>()
            val element = parser.parse(content)
            if (element.isJsonNull) {
                Log.e("content: $content")
                return ERROR_NULL_JSON
            }
            val data = element.asJsonObject["data"]
            data.asJsonArray.forEach { item ->
                docList.add(Document.parse(item.toString()))
            }
            listLength = docList.size
            if (docList.isNotEmpty()) {
//                MongoPersistency.insertMany(docList)
            }
        }
        delay(Random.nextLong(500, 3000))
        return listLength
    }

    private val cookieMap = mutableMapOf<HttpUrl, List<Cookie>>()

    fun start() {
        val builder = OkHttpClient.Builder()
        val client = builder.build()

        runBlocking {
            walkCountryAndRank { requester(client, it) }
        }
    }
}