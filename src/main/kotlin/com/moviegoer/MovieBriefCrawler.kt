package com.moviegoer

import com.moviegoer.db.DocumentParser
import com.moviegoer.db.MongoPersistency
import com.moviegoer.http.HttpRequester
import com.moviegoer.http.UrlProvider
import com.moviegoer.proxy.ProxyPool
import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MovieBriefCrawler {

    fun start() = runBlocking {

        val proxy = ProxyPool()
        val requester = HttpRequester(proxy)
        val parser = DocumentParser()

        var offset = 0
        var isRetry = false
        var url = ""

        while (true) {
            if (!isRetry) {
                // 取得下一个 url
                url = UrlProvider.next(offset)
            } else {
                isRetry = false
            }
            if (url.isEmpty()) {
                Log.i("爬取结束！")
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
            val docList = parser.parseToBriefsList(content)
            if (docList == null) {
                proxy.dropCurrent()
                requester.resetClient()
                isRetry = true
                Log.e("解析失败，重试！url:$url, content:$content")
                continue
            }
            // TODO 批量插入 mongodb
            MongoPersistency.insertManyBriefs(docList)
            // 准备下一轮循环
            val log = { prefix: String -> Log.i("[$prefix]：url:$url") }
            docList.size.let { size ->
                if (size > 0) {
                    log("继续~")
                    offset += size
                } else if (size == 0) {
                    log("完成！")
                    offset = 0
                }
            }
            // 随机延迟
            delay(Random.nextLong(300, 1000))
        }
    }
}