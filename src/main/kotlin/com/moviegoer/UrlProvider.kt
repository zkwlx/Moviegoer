package com.moviegoer

object UrlProvider {

    private const val ONE_PAGE_COUNT = 20

    suspend fun walkCountryAndRank(fetchAction: suspend (String) -> Int) {
        COUNTRY_LIST.forEach { country ->
            for (i in 1..9) {
                var offset = 0
                while (true) {
                    val url = makeUrl(i, i + 1, country, offset)
                    val count = fetchAction(url)
                    if (count < 0) {
                        println("终止：国家=$country, 评分=$i~${i + 1}, offset=$offset, url:$url")
                        return
                    } else if (count < ONE_PAGE_COUNT) {
                        break
                    } else if (count == ONE_PAGE_COUNT) {
                        offset += ONE_PAGE_COUNT
                    } else {
                        println("终止[页数超过20！：$count]：国家=$country, 评分=$i~${i + 1}, offset=$offset, url:$url")
                        return
                    }
                }
            }
        }
    }

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, country: String, offset: Int): String {
        return "https://movie.douban.com/j/new_search_subjects?sort=R" +
                "&range=$rangeBegin,$rangeFinal" +
                "&tags=电影" +
                "&start=$offset" +
                "&countries=$country"
    }


}