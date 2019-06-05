package com.moviegoer

import com.demo.Utils
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.bson.Document
import org.jetbrains.annotations.Nullable
import java.io.IOException
import java.lang.Exception
import kotlin.random.Random

object HttpRequester {
    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    val client = builder.build()

    suspend fun requestBrief(url: String): JsonElement? {
        var element: JsonElement? = null
        try {
            element = request(client, url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return element
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @Nullable
    private suspend fun request(client: OkHttpClient, url: String): JsonElement? {
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
        response = call.execute()
        var element: JsonElement? = null
        response.use {
            val parser = JsonParser()
            val content = it.body()?.string()
            Log.i("------------$content")
            element = parser.parse(content)
            if (element!!.isJsonNull) {
                Log.e("content: $content")
                throw IllegalAccessException("json 解析失败：$content")
            }
        }
        delay(Random.nextLong(500, 3000))
        return element
    }

}