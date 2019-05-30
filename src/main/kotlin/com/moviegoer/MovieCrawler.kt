package com.moviegoer

import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException

class MovieCrawler {

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, country: String, offset: Int): String {
        return "https://movie.douban.com/j/new_search_subjects?sort=R" +
                "&range=$rangeBegin,$rangeFinal" +
                "&tags=电影" +
                "&start=$offset" +
                "&countries=$country"
    }

    public fun start() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(makeUrl(3, 7, "丹麦", 20))
            .addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36"
            )
            .addHeader(
                "Cookie",
                "bid=ddBscCHlUrk; __utmc=30149280; __utmc=223695111; __yadk_uid=PaQqyM2Te9fyjhHQmsiCHsZ3MBvQE61s; _vwo_uuid_v2=DBF439CEC7E34ACC78FC5DE14C1912447|c2ff449617a57e938063cd4fd6cced86; douban-fav-remind=1; trc_cookie_storage=taboola%2520global%253Auser-id%3Da1294574-9538-4169-9145-b1920fbb0e3b-tuct2fc055d; __utmz=30149280.1557643130.59.38.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmz=223695111.1557643130.58.35.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1557652181%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D8VmiRugUBRlr45wRbNjXbT_G81-yQfYV1jou2van-DnZl7XbP383ZaZixkewFAZPdkUrntTRZi-l_ZWdoaFKyK%26wd%3D%26eqid%3Da5fe7d5d000bf06a000000025cd7bf6f%22%5D; _pk_id.100001.4cf6=12a536853689c918.1533477892.59.1557652181.1557643363.; __utma=30149280.1600806281.1533477893.1557643130.1557652181.60; __utma=223695111.135613654.1533477893.1557643130.1557652181.59"
            )
            .method("GET", null)
            .build()

        val new = client.newCall(request)
        new.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val url = call.request().url()
                val parser = JsonParser()
                response.let {
                    println("$url")
                    val content = it.body()?.string()
                    println("---------$content----------")
                    val element = parser.parse(content)
                    element.let { ele ->
                        val a = ele.asJsonObject
                    }
                }
            }

        })


    }

}