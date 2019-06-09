//package com.moviegoer
//
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import org.openqa.selenium.WebElement
//import org.openqa.selenium.chrome.ChromeDriver
//import java.io.File
//
//suspend fun makeAllUrls(driver: ChromeDriver): List<String> {
//    var count = 0
//    var moreTimes = 0
//    while (true) {
//        var more: WebElement
//        try {
//            more = driver.findElementByLinkText("加载更多")
//        } catch (e: Exception) {
//            println("${e.message}")
//            if (moreTimes < 5) {
//                moreTimes++
//                delay(500)
//                continue
//            } else {
//                break
//            }
//        }
//        var times = 0
//        while (true) {
//            try {
//                times++
//                more.click()
//            } catch (e: Throwable) {
//                println("${e.message} --> $times")
//                delay(1000)
//                continue
//            }
//            break
//        }
//        print("${count++},")
//        delay(1000)
//    }
//    println("while $count times!!!")
//    val elements = driver.findElementsByClassName("item")
//    println("====== count ${elements.size}========")
//    val urlList = ArrayList<String>()
//    elements.forEach {
//        val url = it.getAttribute("href")
//        urlList.add(url)
//    }
//    return urlList
//}
//
//suspend fun walkAllUrls(driver: ChromeDriver, urlList: List<String>) {
//    urlList.forEach { url ->
//        driver.navigate().to(url)
//        val name = driver.findElementByXPath("//span[@property='v:itemreviewed']").text
//        val rating = driver.findElementByClassName("rating_num").text
//        var info: String
//        while (true) {
//            info = driver.findElementById("info").text
//            if (info.isEmpty()) {
//                delay(500)
//                continue
//            } else {
//                break
//            }
//        }
//
//        val infoList = info.split("\n")
//        var direct = ""
//        var date = ""
//        var type = ""
//        var madeIn = ""
//        var duration = ""
//        infoList.forEach {
//            val infoItem = it.split(": ")
//            if (infoItem.size == 2) {
//                val a = infoItem[0]
//                val b = infoItem[1]
//                when (a) {
//                    "导演" -> direct = b
//                    "上映日期" -> date = b
//                    "类型" -> type = b
//                    "制片国家/地区" -> madeIn = b
//                    "片长" -> duration = b
//                }
//            }
//        }
//        println("$name - $rating - $direct - $date - $type - $madeIn - $duration")
//        driver.navigate().back()
//        delay(500)
//    }
//}
//
//private fun crawlingAllCondition(driver: ChromeDriver) = runBlocking {
//    var first = true
//    COUNTRY_LIST.forEach { country ->
//        for (i in 1..9) {
//            val url = makeUrl(i, i + 1, country)
//            println(url)
//            driver.get(url)
//            driver.navigate().refresh()
//            delay(5000)
//            val urlList = makeAllUrls(driver)
//            val file = File("./电影/$country[$i-${i + 1}].txt")
//            file.parentFile.mkdirs()
//            if (file.exists()) {
//                file.dropCurrent()
//            }
//            file.createNewFile()
//            val writer = file.bufferedWriter()
//            writer.use { wr ->
//                urlList.forEach { url ->
//                    wr.write(url)
//                    wr.newLine()
//                }
//            }
//            println("$country 电影评分 $i ~ ${i + 1} 爬取完毕，文件：${file.absolutePath}")
////          walkAllUrls(driver, urlList)
//        }
//    }
//}
//
//private fun makeUrl(rangeBegin: Int, rangeFinal: Int, tags: String): String {
//    return "https://movie.douban.com/tag/#/?sort=R&range=$rangeBegin,$rangeFinal&tags=电影,$tags"
//}
//
//
////@Deprecated("Not")
////fun main() {
////    System.setProperty("webdriver.chrome.driver", "/Users/zkw/develop/chromedriver")
////    val driver = ChromeDriver()
////    crawlingAllCondition(driver)
////    driver.close()
////}