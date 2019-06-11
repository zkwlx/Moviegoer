package com.moviegoer

import com.moviegoer.http.UrlProvider
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals

class UrlProviderTest {

    @Test
    fun next_test() {
        //测试正常流程
        val url1 = UrlProvider.next(0)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,2&tags=电影&start=0&countries=中国大陆",
            url1
        )
        val url2 = UrlProvider.next(20)
        val url3 = UrlProvider.next(40)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,2&tags=电影&start=20&countries=中国大陆",
            url2
        )
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,2&tags=电影&start=40&countries=中国大陆",
            url3
        )

        //测试自动增加评分
        val url4 = UrlProvider.next(0)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=2,3&tags=电影&start=0&countries=中国大陆",
            url4
        )

        // 测试 reset
        UrlProvider.reset()
        val url5 = UrlProvider.next(0)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,2&tags=电影&start=0&countries=中国大陆",
            url5
        )

        // 测试断点续传功能
        UrlProvider.setStartPoint(2, 7)
        val url6 = UrlProvider.next(700)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=7,8&tags=电影&start=700&countries=香港",
            url6
        )

        // 测试 setStartPoint 异常处理
        try {
            UrlProvider.setStartPoint(2, 9)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        // 测试自动增加国家 index
        UrlProvider.setStartPoint(2, 8)
        UrlProvider.next(0)
        val url7 = UrlProvider.next(0)
        assertEquals(
            "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,2&tags=电影&start=0&countries=台湾",
            url7
        )
    }

}