package com.moviegoer

import com.moviegoer.db.MongoPersistency
import com.moviegoer.db.MongoPersistency.ERROR_MSG
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class MovieDetailCrawler {

    fun startParallel() = runBlocking {
        val jobs = mutableListOf<Job>()
        repeat(5) {
            jobs.add(GlobalScope.launch {
                start()
            })
        }
        jobs.forEach {
            it.join()
        }
    }

    private var totalCount: AtomicInteger = AtomicInteger(0)

    private suspend fun start() {

        var isRetry = false
        var url = ""

        loop@ while (true) {
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
            when (doc[ERROR_MSG]) {
                "页面不存在" -> {
                    Log.e("页面不存在，跳过！url:$url")
                    continue@loop
                }
                "Json 解析失败" -> {
                    ProxyPool.dropCurrent()
                    HttpRequester.resetClient()
                    isRetry = true
                    Log.e("解析失败，重试！url:$url, content:$content")
                    continue@loop
                }
            }
            MongoPersistency.insertDetail(doc)
            Log.i(doc.toJson())
            val t = totalCount.incrementAndGet()
            if (t % 500 == 0) {
                Log.i("已经爬取 $t 个了！")
            }

            // 随机延迟
            delay(Random.nextLong(300, 1000))
        }
    }
}