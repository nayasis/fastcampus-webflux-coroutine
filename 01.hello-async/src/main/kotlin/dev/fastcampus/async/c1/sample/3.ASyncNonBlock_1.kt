package dev.fastcampus.async.c1.sample

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.system.measureTimeMillis

private val logger = KotlinLogging.logger {}

suspend fun main() {
    measureTimeMillis {
        subA()
        subB()
    }.let { logger.debug { ">> elapsed : $it ms" } }
}

private suspend fun subA() {
    logger.debug { "start sub A" }
    delay(1_000)
    logger.debug { "end sub A" }
}

private suspend fun subB() {
    logger.debug { "start sub B" }
    delay(1_000)
    logger.debug { "end sub B" }
}


