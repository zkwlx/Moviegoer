package com.moviegoer

import com.demo.Utils
import org.junit.Test

class UtilsTest {

    @Test
    fun randomStrTest() {
        println(Utils.randomStr(6))
        println(Utils.randomStr(6))

        println(Utils.randomStr(2))
        println(Utils.randomStr(2))
        println(Utils.randomStr(2))

        println(Utils.randomStr(11))
        println(Utils.randomStr(11))

        println(Utils.randomStr(128))
        println(Utils.randomStr(128))
    }

}