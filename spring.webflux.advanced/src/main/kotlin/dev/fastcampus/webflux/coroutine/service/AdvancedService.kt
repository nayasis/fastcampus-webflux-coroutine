package dev.fastcampus.webflux.coroutine.service

import dev.fastcampus.webflux.coroutine.controller.mono
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

private val logger = KotlinLogging.logger {}

@Service
class AdvancedService{

    suspend fun mdc() {
        logger.debug { "start mdc 1" }
        mdcSub()
        logger.debug { "end mdc 1" }
    }

    private suspend fun mdcSub() {
        logger.debug { "start mdc 2" }
        delay(100)

        Mono.fromCallable {
            logger.debug { "reactor call !!" }
        }.subscribeOn(Schedulers.boundedElastic()).awaitSingle()

        logger.debug { "end mdc 2" }
    }

    @CircuitBreaker(name = "test-circuit", fallbackMethod = "fallback")
    fun unstable(flag: Boolean?): Mono<String> {
        return mono {
            delay(500)
            if(flag == false) {
                throw RuntimeException("failed")
            }
            "success"
        }
    }

    fun fallback(e: Throwable): Mono<String> {
        return mono {
            "fallback response due to ${e.message}"
        }
    }

}


