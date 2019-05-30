package com.moviegoer

import com.demo.Utils
import com.google.gson.JsonParser
import com.moviegoer.UrlProvider.walkCountryAndRank
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import kotlin.random.Random

class MovieCrawler {

    private suspend fun requester(client: OkHttpClient, url: String): Int {
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", AgentProvider.next())
            .addHeader("Cookie", "bid=${Utils.randomStr(11)};")
            .method("GET", null)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val requestUrl = call.request().url()
                val parser = JsonParser()
                response.let {
                    println("$requestUrl")
                    val content = it.body()?.string()
                    println("---------$content----------")
                    val element = parser.parse(content)
                    element.let { ele ->
                        val a = ele.asJsonObject
                    }
                }
            }

        })
        delay(Random.nextLong(50, 500))
        return 0
    }

    fun start() {
        val client = OkHttpClient()

        runBlocking {
            walkCountryAndRank { requester(client, it) }
        }
    }
}