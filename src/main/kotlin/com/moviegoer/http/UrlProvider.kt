package com.moviegoer.http

import java.util.*

object UrlProvider {

    // 等于 0 的时候，rank 从 1 分开始爬取，等于 -1 的时候，rank 从 0 分开始爬取
    private const val INITIAL_RANK = 0

    // 分数分为，等于 9 时，最大分数小于 10
    private const val MAX_RANK = 9

    private val INITIAL_YEAR = Calendar.getInstance().get(Calendar.YEAR)
    private const val MIN_YEAR = 1900

    private var countryIndex = 0
    private var rank = INITIAL_RANK

    //TODO INITIAL_RANK 和 MAX_RANK 是临时修改，只获取评分为 0~1 的电影
    private var year = INITIAL_YEAR + 1

    /**
     * 根据 offset 自动创建下一个 url，当返回空字符串时，说明遍历结束，没有 next 了
     */
    fun next(offset: Int): String {
        if (offset == 0) {
            nextRankOrCountry()
        }
        return if (countryIndex >= COUNTRY_LIST.size) ""
        else makeUrl(
            rank,
            rank + 1,
            COUNTRY_LIST[countryIndex],
            offset
        )

        //用于查看 IP 代理是否生效
//        return "http://pv.sohu.com/cityjson"
    }

    /**
     * 根据 offset 自动创建下一个 url，当返回空字符串时，说明遍历结束，没有 next 了
     */
    fun nextForYear(offset: Int): String {
        if (offset == 0) {
            nextCountryOrYear()
        }
        return if (countryIndex >= COUNTRY_LIST.size) ""
        else makeUrl(
            rank,
            rank + 1,
            COUNTRY_LIST[countryIndex],
            offset,
            year
        )
    }

    /**
     * 断点续传功能
     */
    fun setStartPoint(countryIndex: Int, rank: Int): UrlProvider {
        if (countryIndex >= COUNTRY_LIST.size || rank >= MAX_RANK) {
            throw IllegalArgumentException("传入的参数有误，countryIndex：$countryIndex，rank：$rank")
        }
        UrlProvider.countryIndex = countryIndex
        UrlProvider.rank = rank
        return this
    }

    fun reset() {
        countryIndex = 0
        rank = INITIAL_RANK
    }

    private fun nextRankOrCountry() {
        if (rank == MAX_RANK) {
            countryIndex += 1
            rank = INITIAL_RANK + 1
        } else {
            rank += 1
        }
    }

    private fun nextCountryOrYear() {
        if (year == MIN_YEAR) {
            year = INITIAL_YEAR
            countryIndex += 1
        } else {
            if (year <= 2000) {
                year = MIN_YEAR
            } else {
                --year
            }
        }
    }

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, country: String, offset: Int): String {
        return "https://movie.douban.com/j/new_search_subjects?range=$rangeBegin,$rangeFinal" +
                "&start=$offset" +
                "&countries=$country" +
                "&tags=电影&sort=R"
    }

    private fun makeUrl(rangeBegin: Int, rangeFinal: Int, country: String, offset: Int, year: Int): String {
        if (year == MIN_YEAR) {
            return "https://movie.douban.com/j/new_search_subjects?range=$rangeBegin,$rangeFinal" +
                    "&start=$offset" +
                    "&countries=$country" +
                    "&year_range=$year,1999" +
                    "&tags=电影&sort=R"
        } else {
            return "https://movie.douban.com/j/new_search_subjects?range=$rangeBegin,$rangeFinal" +
                    "&start=$offset" +
                    "&countries=$country" +
                    "&year_range=$year,$year" +
                    "&tags=电影&sort=R"
        }
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