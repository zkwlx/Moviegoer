package com.moviegoer

import com.moviegoer.utils.Log

var USE_PROXY = true

fun main() {
    MovieBriefCrawler().start()

    Log.i("----------------------brief end, start to detail -------------------")

//    MovieDetailCrawler().startParallel()

//    MovieDetailFixCrawler().startParallel()
}