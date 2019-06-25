package com.moviegoer

import com.moviegoer.db.DocumentParser
import com.moviegoer.db.MongoPersistency
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.*
import java.net.URL
import kotlin.random.Random

class MovieDetailFixCrawler {

    fun startParallel() = runBlocking {
        val jobs = mutableListOf<Job>()
        repeat(10) {
            jobs.add(GlobalScope.launch {
                start()
            })
        }
        jobs.forEach {
            it.join()
        }
    }

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
            // 获取 url 的 http 请求返回值
            val content = requester.syncRequest(url)
            if (content == null) {
                proxy.dropCurrent()
                isRetry = true
                Log.e("请求失败，重试！content == null： $url")
                continue
            }
            // 将 json 解析成 document list
            val countryList = parser.parseToCountry(content)
            if (countryList[0] == "页面不存在") {
                Log.i("页面不存在：$url")
                continue
            } else if (countryList[0] == "IP 异常") {
                proxy.dropCurrent()
                isRetry = true
                Log.e("请求失败，重试！content == null： $url")
                continue
            }
            val path = URL(url).path
            val year = findDateToYear(path)
            var yearDoc: Pair<String, Int>? = null
            if (year != null) {
                yearDoc = Pair("yearPublished", year)
            }
            var countryDoc: Pair<String, List<String>>? = null
            var countryStr = "empty"
            if (countryList.isEmpty()) {
                Log.i("===> $url ---> ${content.substring(0, 1024)}")
            } else {
                countryStr = countryList.reduce { acc, s -> "$acc, $s" }
                countryDoc = Pair("country", countryList)
            }

            Log.i("国家列表：$countryStr, 发布年份：$year, $url")

            MongoPersistency.setAnyDetail(path, yearDoc, countryDoc)

            // 随机延迟
            delay(Random.nextLong(300, 1000))
        }
    }

    private fun findDateToYear(path: String): Int? {
        val findIterable = MongoPersistency.findDetail(path)
        val doc = findIterable.first()
        return if (doc != null) {
            val date = doc["datePublished"] as String
            date.split("-")[0].toIntOrNull()
        } else {
            -1
        }
    }

}