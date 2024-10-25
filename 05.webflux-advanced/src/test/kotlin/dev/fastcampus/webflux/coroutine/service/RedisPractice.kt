package dev.fastcampus.webflux.coroutine.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Range
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation
import org.springframework.data.redis.core.*
import org.springframework.test.context.ActiveProfiles
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class RedisPractice(
    private val template: ReactiveRedisTemplate<Any,Any>
): StringSpec({

    val KEY = "key"

    afterTest {
        template.delete(KEY).awaitSingle()
    }

    "hello reactive redis" {

        val ops = template.opsForValue()

        ops.setAndAwait(KEY, "fastcampus")
        ops.getAndAwait(KEY) shouldBe "fastcampus"

        logger.debug { "key: ${ops.get(KEY).block()}" }

//        template.expireAndAwait(KEY, 3.seconds.toJavaDuration())
        template.expire(KEY, 3.seconds)

        delay(5.seconds)

        ops.getAndAwait(KEY) shouldBe null

    }

    "list" {
        val ops = template.opsForList()
        ops.rightPushAllAndAwait(KEY, 1,2,3,4)
        ops.leftPushAllAndAwait(KEY, 5,6,7,8)

        ops.sizeAndAwait(KEY) shouldBe 8

//        val items = ops.rangeAsFlow(KEY, 0, -1).toList()
        val items = ops.toList(KEY)
        logger.debug { "items: $items" }

        ops.rightPop(KEY, 2).asFlow().toList() shouldBe listOf(4,3)
        ops.leftPop(KEY, 2).asFlow().toList() shouldBe listOf(8,7)

        ops.toList(KEY) shouldBe listOf(6,5,1,2)
    }

    "lru" {
        val ops = template.opsForList()
        ops.rightPushAllAndAwait(KEY, 1,2,3,4,5,6,7,8,9)

        ops.removeAndAwait(KEY,0, 7)
        ops.leftPushAndAwait(KEY,7)
        ops.toList(KEY).let { logger.debug { it } }

        ops.removeAndAwait(KEY,0, 4)
        ops.leftPushAndAwait(KEY,4)
        ops.toList(KEY).let { logger.debug { it } }
    }

    "hash" {
        val map = template.opsForHash<String,String>()
        map.putAndAwait(KEY, "A", "amazon")
        map.putAndAwait(KEY, "Z", "zero")

        map.getAndAwait(KEY, "A") shouldBe "amazon"
        map.getAndAwait(KEY, "Z") shouldBe "zero"
        map.getAndAwait(KEY, "C") shouldBe null
    }

    "sorted set" {
        val ops = template.opsForZSet()

        listOf(
            "jake" to 90,
            "john" to 10,
            "jane" to 40,
        ).forEach {
            ops.addAndAwait(KEY, it.first, it.second * 1.0)
        }
        ops.toList(KEY).let { logger.debug { it } }

        ops.addAndAwait(KEY, "tom", 66.0)
        ops.toList(KEY).let { logger.debug { it } }

        ops.addAndAwait(KEY, "john", 99.0)
        ops.toList(KEY).let { logger.debug { it } }

        ops.popMax(KEY).awaitSingleOrNull()?.value shouldBe "john"
        ops.toList(KEY).let { logger.debug { it } }

        ops.popMin(KEY).awaitSingleOrNull()?.value shouldBe "jane"
        ops.toList(KEY).let { logger.debug { it } }

    }

    "geo redis" {
        val ops = template.opsForGeo()

        listOf(
            GeoLocation("seoul",   Point(126.97806, 37.56667)),
            GeoLocation("busan",   Point(129.07556, 35.17944)),
            GeoLocation("incheon", Point(126.70528, 37.45639)),
            GeoLocation("daegu",   Point(128.60250, 35.87222)),
            GeoLocation("anyang",  Point(126.95556, 37.39444)),
            GeoLocation("daejeon", Point(127.38500, 36.35111)),
            GeoLocation("gwangju", Point(126.85306, 35.15972)),
            GeoLocation("suwon",   Point(127.02861, 37.26389)),
        ).forEach {
            @Suppress("UNCHECKED_CAST")
            ops.addAndAwait(KEY, it as GeoLocation<Any>)
        }

        ops.distanceAndAwait(KEY, "seoul", "busan")?.let { logger.debug{"seoul -> busan : ${it.value}"}}

        val p = ops.positionAndAwait(KEY, "daegu")!!

        val circle = Circle(p, Distance(200.0, Metrics.KILOMETERS))

        ops.radius(KEY, circle).asFlow().map { it.content.name }.toList().let {
            logger.debug { "cities near daegu : $it" }
        }

    }

    "hyper loglog" {
        val ops = template.opsForHyperLogLog()
        ops.add(KEY,"192.179.0.23","41.61.2.230","225.105.161.131").awaitSingle()
        ops.add(KEY,"1.1.1.1","2.2.2.2").awaitSingle()
        ops.add(KEY,"9.9.9.9").awaitSingle()
        ops.add(KEY,"8.8.8.8").awaitSingle()
        ops.add(KEY,"7.7.7.7","2.2.2.2","1.1.1.1").awaitSingle()
        ops.sizeAndAwait(KEY).let { logger.debug{ "count: $it"} }
    }

    "pub/sub" {

        template.listenToChannel("ch-1").doOnNext {
            logger.debug { ">> received ch-1: ${it.message}" }
        }.subscribe()

        template.listenToChannel("ch-2").doOnNext {
            logger.debug { ">> received ch-2: ${it.message}" }
        }.subscribe()

        template.listenToChannel("ch-3").asFlow().onEach {
            logger.debug { ">> received ch-3: ${it.message}" }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        repeat(10) {
            val message = "test message $it"
            val dest    = "ch-${it % 3 + 1}"
            template.convertAndSend(dest, message).awaitSingle()
            delay(1_000)
        }

    }


})

suspend fun ReactiveRedisTemplate<Any,Any>.expire(key: Any, ttl: Duration) {
    this.expireAndAwait(key, ttl.toJavaDuration())
}

suspend fun ReactiveListOperations<Any,Any>.toList(key: Any): List<Any> {
    return this.rangeAsFlow(key,0,-1).toList()
}

suspend fun ReactiveZSetOperations<Any,Any>.toList(key: Any): List<Any> {
    return this.rangeAsFlow(key,Range.closed(0,-1)).toList()
}

