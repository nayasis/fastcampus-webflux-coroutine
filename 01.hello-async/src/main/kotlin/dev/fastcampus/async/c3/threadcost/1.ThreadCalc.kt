package dev.fastcampus.async.c3.threadcost

import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

fun main() {
    val count    = 2_000
    val executor = Executors.newFixedThreadPool(count)
    val latcher  = CountDownLatch(count)
    val starter  = CountDownLatch(1)

    repeat(count) {
        executor.submit {
            starter.await() // wait for start
            repeat(count) {
                sleep(1)
            }
            latcher.countDown()
        }
    }

    measureTimeMillis {
        starter.countDown() // start
        latcher.await()
    }.let { println(">> elapsed: ${it}ms") }

    executor.shutdown()
}