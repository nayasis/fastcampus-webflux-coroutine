package dev.fastcampus.async.c3.threadcost

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    val count = 2_000
    measureTimeMillis { runBlocking {
        repeat(count) {
            launch(Dispatchers.IO) {
                repeat(count) {
                    delay(1)
                }
            }
        }
    }}.let { println(">> elapsed: ${it}ms") }
}