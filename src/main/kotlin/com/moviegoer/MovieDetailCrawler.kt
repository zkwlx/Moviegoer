package com.moviegoer

import com.moviegoer.db.MongoPersistency
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MovieDetailCrawler {

    fun startFast() {
        repeat(5) {
            GlobalScope.launch {
                start()
            }
        }
    }

    private suspend fun start() {
        var isRetry = false
        var url = ""

        while (true) {
            if (!isRetry) {
                // 取得下一个 url
                url = ""
                val doc = MongoPersistency.nextBrief()
                doc?.let {
                    url = it.getString("url")
                }
            } else {
                isRetry = false
            }
            if (url.isEmpty()) {
                Log.i("爬取结束 --> ${Thread.currentThread().name}")
                break
            }
            // 获取 url 的 http 请求返回值
            val content = HttpRequester.syncRequest(url)
            if (content == null) {
                ProxyPool.dropCurrent()
                isRetry = true
                Log.e("请求失败，重试！content == null： $url")
                continue
            }
            // 将 json 解析成 document list
            val doc = MongoPersistency.parseToDetail(content)
            if (doc == null) {
                ProxyPool.dropCurrent()
                HttpRequester.resetClient()
                isRetry = true
                Log.e("解析失败，重试！url:$url, content:$content")
                continue
            }
            MongoPersistency.insertDetail(doc)

            // 随机延迟
            delay(Random.nextLong(300, 1000))
        }
    }
}