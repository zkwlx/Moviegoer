package com.moviegoer

import com.moviegoer.db.MongoPersistency
import com.moviegoer.http.HttpRequester
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MovieDetailCrawler {

    fun start() = runBlocking {
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
                Log.i("爬取结束！")
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
            //TODO 要改
            // 将 json 解析成 document list
            val docList = MongoPersistency.parseToDocumentList(content)
            if (docList == null) {
                ProxyPool.dropCurrent()
                HttpRequester.resetClient()
                isRetry = true
                Log.e("解析失败，重试！url:$url, content:$content")
                continue
            }
            // TODO 要改
            // TODO 批量插入 mongodb
            MongoPersistency.insertManyBriefs(docList)

            // 随机延迟
            delay(Random.nextLong(300, 2000))
        }
    }
}