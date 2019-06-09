package com.moviegoer

import com.moviegoer.utils.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.SocketAddress

object ProxyPool {

    private const val POOL_URL = "http://127.0.0.1:5010/"
    private const val GET_METHOD = "get"
    private const val GET_ALL_METHOD = "get_all"
    private const val GET_STATUS_METHOD = "get_status"
    private const val DELETE_METHOD = "dropCurrent"

    private val client = OkHttpClient.Builder().build()

    private var currentProxy: Proxy = Proxy.NO_PROXY

    fun get(): Proxy? {
        if (currentProxy != Proxy.NO_PROXY) {
            return currentProxy
        }
        var times = 5
        var content: String? = null
        while (times > 0) {
            content = request(POOL_URL + GET_METHOD)
            if (content.isNullOrEmpty()) {
                times--
            } else {
                break
            }
        }
        if (content.isNullOrEmpty()) {
            return Proxy.NO_PROXY
        }
        val ipAndPort = content.split(":")
        val address = InetSocketAddress(ipAndPort[0], ipAndPort[1].toInt())
        currentProxy = Proxy(Proxy.Type.HTTP, address)

        return currentProxy
    }

    fun dropCurrent(address: SocketAddress?) {
        currentProxy = Proxy.NO_PROXY
        if (address is InetSocketAddress) {
            val arg = "${address.hostName}:${address.port}"
            Log.i("dropCurrent: $arg")
            request("$POOL_URL$DELETE_METHOD/?proxy=$arg")
        }
    }

    fun dropCurrent() {
        val address = currentProxy.address()
        dropCurrent(address)
    }

    private fun request(url: String): String? {
        val request = Request.Builder().url(url).method("GET", null).build()
        val call = client.newCall(request)
        val response = call.execute()
        return response.use {
            it.body()?.string()
        }
    }


}