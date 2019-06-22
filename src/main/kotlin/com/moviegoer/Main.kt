package com.moviegoer

var USE_PROXY = true

fun main() {
//    MovieBriefCrawler().start()

    MovieDetailCrawler().startParallel()

}