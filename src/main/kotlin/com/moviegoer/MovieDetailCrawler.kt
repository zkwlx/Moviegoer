package com.moviegoer

import com.moviegoer.db.DocumentParser
import com.moviegoer.db.MongoPersistency
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.*
import org.bson.Document
import java.net.URL
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

        val proxy = ProxyPool()
        val requester = HttpRequester(proxy)
        val parser = DocumentParser()

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
            val u = URL(url)
            if (MongoPersistency.isExist(u.path)) {
                Log.i("跳过，已存在: $url")
                continue
            }
            // 获取 url 的 http 请求返回值
            val content = requester.syncRequest(url)
            if (content == null) {
                proxy.dropCurrent()
                isRetry = true
                Log.e("请求失败，重试！content == null： $url")
                continue
            }
            // 将 json 解析成 document list
            val doc = parser.parseToDetail(content)
            when (doc[parser.ERROR_MSG]) {
                "页面不存在" -> {
                    Log.e("页面不存在，跳过！url:$url")
                    continue@loop
                }
                "Json 解析失败" -> {
                    proxy.dropCurrent()
                    requester.resetClient()
                    isRetry = true
                    Log.e("解析失败，重试！url:$url")
                    continue@loop
                }
            }
            // 解析出国家地区
            val countryList = parser.parseToCountry(content)
            if (countryList.isEmpty()) {
                Log.i("========> $url ---> $content")
            } else {
                if (countryList[0] == "页面不存在") {
                    Log.i("页面不存在：$url")
                    continue@loop
                } else if (countryList[0] == "IP 异常") {
                    proxy.dropCurrent()
                    isRetry = true
                    Log.e("请求失败，重试！content == null： $url")
                    continue@loop
                }
            }
            var countryStr = "empty"
            if (countryList.isNotEmpty()) {
                countryStr = countryList.reduce { acc, s -> "$acc, $s" }
                doc["country"] = countryList
            }

            val year = findDateToYear(doc)
            year?.let {
                doc["yearPublished"] = year
            }
            Log.i("国家列表：$countryStr, 发布年份：$year, $url")

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

    private fun findDateToYear(doc: Document): Int? {
        val value = doc["datePublished"] ?: return -1
        val date = value as String
        return date.split("-")[0].toIntOrNull()
    }

}