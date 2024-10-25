package dev.fastcampus.webflux.coroutine

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@Component
class Locker(
    template: ReactiveRedisTemplate<Any,Any>
) {

    private val localLock = ConcurrentHashMap<String,Boolean>()

    private val ops = template.opsForValue()

    suspend fun <T> lock(key: String, work: suspend () -> T): T {
        if(!tryLock(key))
            throw TimeoutException("lock timed out")
        try {
            return work.invoke()
        } finally {
            unlock(key)
        }
    }

    private suspend fun tryLock(key: String): Boolean {
        val start = System.nanoTime()

        // redisson
        // ops.tryLock()

        while (
            ! localLock.contains(key) &&
            ! ops.setIfAbsent(key, true, 10.seconds.toJavaDuration()).awaitSingle()
        ) {
            logger.debug { "- spin lock : $key" }
            delay(100.milliseconds)
            val elapsed = (System.nanoTime() - start).nanoseconds
            if(elapsed >= 10.seconds) {
                return false
            }
        }
        localLock[key] = true
        return true
    }

    private suspend fun unlock(key: String) {
        try {
            ops.delete(key).awaitSingle()
        } catch (e: Exception) {
            logger.warn(e) { "${e.message}" }
        } finally {
            localLock.remove(key)
        }
    }

}