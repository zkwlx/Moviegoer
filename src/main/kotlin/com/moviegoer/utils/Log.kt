package com.moviegoer.utils

object Log {

    fun i(s: String) {
        println("[INFO] - $s")
    }

    fun e(s: String) {
        println("[ERROR] - $s")
    }

}