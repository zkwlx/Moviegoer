package com.moviegoer

import com.moviegoer.db.DocumentParser
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import org.junit.Test

class CrawlerTest {

    @Test
    fun requestAndParserTest() {
        val url = "https://movie.douban.com/subject/2117810/"
        USE_PROXY = false
        val requester = HttpRequester(ProxyPool())
        val parser = DocumentParser()
        // 获取 url 的 http 请求返回值
        val content = requester.syncRequest(url)
        if (content == null) {
            Log.e("请求失败!")
        } else {
            Log.i("----------------\n$content\n-----------------")
            // 将 json 解析成 document list
            val doc = parser.parseToDetail(content)
            when (doc[parser.ERROR_MSG]) {
                "Json 解析失败" -> {
                    Log.e("解析失败!")
                }
            }
        }
    }

    @Test
    fun test() {
        val str = "a\nb\nc\nd\ne\nf\ng\nh\ni"
        run foreach@{
            str.lineSequence().forEach {
                if (it == "f") {
                    return@foreach
                }
                println(it)
            }
        }

    }

}