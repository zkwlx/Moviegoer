package com.moviegoer

var USE_PROXY = false

fun main() {
//    MovieBriefCrawler().start()

    MovieDetailCrawler().startParallel()

//    MovieDetailFixCrawler().startParallel()
}