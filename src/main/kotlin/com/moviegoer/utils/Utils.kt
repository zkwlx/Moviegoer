package com.demo

import java.lang.StringBuilder
import kotlin.random.Random

object Utils {

    private const val letterAndDigits = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val letterAndDigitsCount = letterAndDigits.length

    /**
     * 生成长度为 [length] 的随机字符串，字符串内容可能包括大小写字母和数字
     */
    fun randomStr(length: Int): String {
        val sb = StringBuilder(length)
        repeat(length) {
            val randomIndex = Random.nextInt(letterAndDigitsCount)
            sb.append(letterAndDigits[randomIndex])
        }
        return sb.toString()
    }

}