package com.moviegoer

import com.moviegoer.utils.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MovieCrawler {

    private val COUNT = 20

    fun start() = runBlocking {

        var offset = 0
        // 设置续传点
        UrlProvider.setStartPoint(2, 7)
        offset = 760

        var loopTimes = 0

        while (true) {
            // 取得下一个 url
            val url = UrlProvider.next(offset)
            // 获取 url 的 http 请求返回值
            val content = HttpRequester.requestBrief(url)
            if (content == null) {
                Log.e("请求失败，content == null： $url")
                break
            }
            // 将 json 解析成 document list
            val docList = MongoPersistency.parseToDocumentList(content)
            if (docList == null) {
                Log.e("解析失败，url:$url, content:$content")
                break
            }
            // 批量插入 mongodb
            MongoPersistency.insertMany(docList)
            // 准备下一轮循环
            val log = { prefix: String -> Log.i("[$prefix]：url:$url") }
            if (docList.size < COUNT) {
                log("完成！")
                offset = 0
            } else if (docList.size == COUNT) {
                log("继续~")
                offset += COUNT
            } else {
                log("终止_页数超过20!")
                break
            }

            if (loopTimes > 330) {
                loopTimes = 0
                HttpRequester.resetClient()
                Log.i("请求好多次了，休息一下~")
                // 每 300 次休息1分钟
                delay(60000)
            } else {
                loopTimes++
                // 随机延迟
                delay(Random.nextLong(1000, 5000))
            }
        }
    }
}