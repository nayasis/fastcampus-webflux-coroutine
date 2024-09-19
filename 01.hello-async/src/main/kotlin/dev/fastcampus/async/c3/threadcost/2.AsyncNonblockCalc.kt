package dev.fastcampus.async.c3.threadcost

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

fun main() {
    val sum = AtomicLong()
    measureTimeMillis {
        runBlocking {
            repeat(2_000) {
                launch {
                    repeat(100_000) {
                        sum.addAndGet(1L)
                    }
                }
            }
        }
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}