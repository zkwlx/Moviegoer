package com.moviegoer

import com.moviegoer.db.DocumentParser
import org.junit.Test

class DocumentParserTest {

    @Test
    fun parseToCountryTest() {
        val parser = DocumentParser()

        val test = "        \n<span class=\"pl\">制片国家/地区:</span> 美国 / 加拿大 <br/>\n"

        val countrys = parser.parseToCountry(test)

        countrys?.forEach { println(it) }

        val test2 =
            "<span ><span class='pl'>导演</span>: <span class='attrs'><a href=\"/celebrity/1022982/\" rel=\"v:directedBy\">西蒙·金伯格</a></span></span><br/>"
        val result = parser.parseToCountry(test2)
        assert(result!!.isEmpty())
    }

}