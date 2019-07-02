package com.moviegoer

import com.moviegoer.utils.Log

var USE_PROXY = true

fun main() {
//    MovieBriefCrawler().start()


    MovieDetailCrawler().startParallel()
    Log.i("---------------------- end -------------------")

//    MovieDetailFixCrawler().startParallel()
}