package com.moviegoer

import okhttp3.*
import java.lang.Exception

object HttpRequester {

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    private lateinit var client: OkHttpClient

    private val cookieMap = mutableMapOf<HttpUrl, MutableList<Cookie>>()

    init {
        resetClient()
    }

    fun resetClient() {
        client = builder.build()
//        client = builder.cookieJar(object : CookieJar {
//            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
//                cookieMap[url] = cookies
//            }
//            override fun loadForRequest(url: HttpUrl): MutableList<Cookie>? {
//                return cookieMap[url]
//            }
//        }).build()
    }

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
    private fun request(url: String): String? {
        val request = Request.Builder().url(url).method("GET", null)
            .addHeader("User-Agent", AgentProvider.next())
//            .addHeader("Cookie", "bid=${Utils.randomStr(11)};")
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