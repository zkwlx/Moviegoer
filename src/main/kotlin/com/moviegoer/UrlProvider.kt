package com.moviegoer

object UrlProvider {

    private var countryIndex = 0
    // 跳过0~1分数的电影，都是没有评分的电影
    private var rank = -1

    /**
     * 根据 offset 自动创建下一个 url，当返回空字符串时，说明遍历结束，没有 next 了
     */
    fun next(offset: Int): String {
        if (offset == 0) {
            nextRank()
        }
        return if (rank >= 10) ""
        else makeUrl(rank, rank + 1, offset)

        //用于查看 IP 代理是否生效
//        return "http://pv.sohu.com/cityjson"
    }

    /**
     * 断点续传功能
     */
    fun setStartPoint(countryIndex: Int, rank: Int): UrlProvider {
        if (countryIndex >= COUNTRY_LIST.size || rank >= 9) {
            throw IllegalArgumentException("传入的参数有误，countryIndex：$countryIndex，rank：$rank")
        }
        this.countryIndex = countryIndex
        this.rank = rank
        return this
    }

    fun reset() {
        countryIndex = 0
        rank = -1
    }

    private fun nextRankOrCountry() {
        if (rank == 9) {
            countryIndex += 1
            rank = 1
        } else {
            rank += 1
        }
    }

    private fun nextRank() {
        rank += 1
    }

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, country: String, offset: Int): String {
        return "https://movie.douban.com/j/new_search_subjects?sort=R" +
                "&range=$rangeBegin,$rangeFinal" +
                "&tags=电影" +
                "&start=$offset" +
                "&countries=$country"
    }

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, offset: Int): String {
        return "https://movie.douban.com/j/new_search_subjects?sort=R" +
                "&range=$rangeBegin,$rangeFinal" +
                "&tags=电影" +
                "&start=$offset"
    }


//    suspend fun walkCountryAndRank(fetchAction: suspend (String) -> Int) {
//        COUNTRY_LIST.forEach { country ->
//            if (isStartPointCountry(country)) {
//                var countryCount = 0
//                for (i in 1..9) {
//                    if (!isStartPointRank(i)) {
//                        continue
//                    }
//                    var offset = getStartPointOffset()
//                    while (true) {
//                        val url = makeUrl(i, i + 1, country, offset)
//                        val count = fetchAction(url)
//                        countryCount += count
//                        val log =
//                            { prefix: String -> Log.i("[$prefix]：国家=$country, 评分=$i~${i + 1}, offset=$offset, count=$count url:$url") }
//                        if (count < 0) {
//                            log("终止")
//                            return
//                        } else if (count < ONE_PAGE_COUNT) {
//                            log("完成!")
//                            break
//                        } else if (count == ONE_PAGE_COUNT) {
//                            offset += ONE_PAGE_COUNT
//                            log("继续~")
//                        } else {
//                            log("终止_页数超过20!")
//                            return
//                        }
//                    }
//                }
//                Log.i("---> $country 结束，总共 $countryCount 个 <---")
//            }
//        }
//    }
//
//    /**---------------用于设置中断回复点----------------**/
//    private var foundedStartPoint = false
//    private const val COUNTRY = "香港"
//    private const val RANK = 7
//    private const val OFFSET = 760
//
//    private fun isStartPointCountry(country: String): Boolean {
//        return if (foundedStartPoint) {
//            true
//        } else country == COUNTRY
//    }
//
//    private fun isStartPointRank(rank: Int): Boolean {
//        return if (foundedStartPoint) {
//            true
//        } else rank == RANK
//    }
//
//    private fun getStartPointOffset(): Int {
//        return if (foundedStartPoint) {
//            0
//        } else {
//            foundedStartPoint = true
//            OFFSET
//        }
//    }
//
//    /**---------------用于设置中断回复点----------------**/
}