package com.moviegoer

import com.demo.Utils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.annotations.Nullable
import java.lang.Exception

object HttpRequester {

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val client = builder.build()

    fun requestBrief(url: String): String? {
        var content: String? = null
        try {
            content = request(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return content
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @Nullable
    private fun request(url: String): String? {
        val request = Request.Builder().url(url).method("GET", null)
            .addHeader("User-Agent", AgentProvider.next())
            .addHeader("Cookie", "bid=${Utils.randomStr(11)};")
            .build()
        val call = client.newCall(request)
        val response: Response
        //TODO 增加重试功能
        response = call.execute()
        return response.use {
            it.body()?.string()
        }
    }

}