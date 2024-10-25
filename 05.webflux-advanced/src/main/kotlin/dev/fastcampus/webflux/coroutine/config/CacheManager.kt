package dev.fastcampus.webflux.coroutine.config

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.deleteAndAwait
import org.springframework.data.redis.core.getAndAwait
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger{}

@Component
class CacheManager(
    private val redisTemplate: ReactiveRedisTemplate<Any,Any>
) {

    private val ops = redisTemplate.opsForValue()

    val TTL = HashMap<String,Duration>()

    suspend fun set(key: String, value: Any) {
        val ttl = TTL[key]
        if(ttl == null) {
            ops.set(key,value)
        } else {
            ops.set(key,value,ttl.toJavaDuration())
        }.awaitSingleOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> get(key: String): T? {
        return ops.getAndAwait(key)?.let {
            it as T
        }
    }

    suspend fun delete(key: String) {
        ops.deleteAndAwait(key)
    }

    suspend fun deleteAll(pattern: String) {
        redisTemplate.keys("*$pattern*").asFlow().onEach { key ->
            redisTemplate.delete(key).awaitFirstOrNull()
        }.collect()
    }

}