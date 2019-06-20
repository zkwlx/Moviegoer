package com.moviegoer

import com.moviegoer.db.MongoPersistency
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.ConcurrentLinkedQueue

class MongoPersistencyTest {

    @Test
    fun nextBriefTest() {

        val c = ConcurrentLinkedQueue<String>()

        runBlocking {
            repeat(10) {
                launch {
                    delay(10)
                    repeat(20) {
                        val doc = MongoPersistency.nextBrief()
                        doc?.let {
                            val s = it.getString("url")
                            if (c.contains(s)) {
                                println("重复啦：$s")
                            } else {
                                c.add(s)
                            }
                        }
                    }

                }
            }

        }

    }
}