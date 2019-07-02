package com.moviegoer.http

import com.demo.Utils
import com.moviegoer.USE_PROXY
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.net.*
import java.util.concurrent.TimeUnit

class HttpRequester(val proxy: ProxyPool) {

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    private lateinit var client: OkHttpClient

    init {
        resetClient()
    }

    fun resetClient() {
        if (USE_PROXY) {
            client = builder.proxySelector(object : ProxySelector() {
                override fun select(uri: URI?): MutableList<Proxy> {
                    val proxy = proxy.get()
                    Log.i("proxy: $proxy")
                    return if (proxy == null) {
                        mutableListOf()
                    } else {
                        mutableListOf(proxy)
                    }
                }

                override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
                    proxy.dropCurrent(sa)
                }
            }).connectTimeout(3, TimeUnit.SECONDS).build()
        } else {
            client = builder.build()
        }
    }

    fun syncRequest(url: String): String? {
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
            .addHeader("Cookie", "bid=${Utils.randomStr(11)};")
            .build()
        var response: Response? = null
        //TODO 增加重试功能，失败抛异常
        var times = 25
        while (times > 0) {
            val call = client.newCall(request)
            try {
                response = call.execute()
            } catch (e: Exception) {
                Log.e("request error $e: ${e.message}")
                proxy.dropCurrent()
                times--
                continue
            }
            break
        }
        return response.use {
            it?.body()?.string()
        }
    }

}