package com.moviegoer

import com.moviegoer.http.HttpRequester
import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MovieCrawler {

    fun start() = runBlocking {
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
            val content = HttpRequester.requestBrief(url)
            if (content == null) {
                ProxyPool.dropCurrent()
                isRetry = true
                Log.e("请求失败，重试！content == null： $url")
                continue
            }
            // 将 json 解析成 document list
            val docList = MongoPersistency.parseToDocumentList(content)
            if (docList == null) {
                ProxyPool.dropCurrent()
                HttpRequester.resetClient()
                isRetry = true
                Log.e("解析失败，重试！url:$url, content:$content")
                continue
            }
            // TODO 批量插入 mongodb
            MongoPersistency.insertMany(docList)
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
            delay(Random.nextLong(500, 3000))

//            if (loopTimes > 521) {
//                loopTimes = 0
//                HttpRequester.resetClient()
//                Log.i("请求好多次了，休息一下~")
//                // 每 300 次休息1分钟
//                delay(60000)
//            } else {
        }
    }
}