package dev.fastcampus.webflux.coroutine.controller

import dev.fastcampus.webflux.coroutine.service.AdvancedService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.coroutines.Continuation

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val service: AdvancedService
) {

    @GetMapping("/test/txid")
    suspend fun testTxId() {
//        withContext(MDCContext()) {
            logger.debug { "hello txid" }
            delay(100)
            service.mdc()
            logger.info { "end txid test" }
//        }
    }

    @PostMapping("/test/txid2")
    fun testNonCoroutineTxid() {
        logger.debug { "hello txid with non-coroutine" }
    }

}

@Aspect
@Component
class AdvancedAspectConfig {

    @Around("""
        @annotation(org.springframework.web.bind.annotation.RequestMapping) ||
        @annotation(org.springframework.web.bind.annotation.GetMapping) ||
        @annotation(org.springframework.web.bind.annotation.PostMapping) ||
        @annotation(org.springframework.web.bind.annotation.PutMapping) ||
        @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
        @annotation(org.springframework.web.bind.annotation.PatchMapping)
    """)
    fun bindMdcContext(jp: ProceedingJoinPoint): Any? {

        logger.debug { """
            >> bind MDC context
            - method: ${jp.signature}
            - args:   ${jp.args.toList()}
        """.trimIndent() }

        return if(jp.hasSuspendFunction) {
            val continuation = jp.args.last() as Continuation<*>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it) }
            val newArgs = jp.args.dropLast(1) + newContinuation

            jp.proceed(newArgs.toTypedArray())
        } else {
            jp.proceed()
        }
    }


    private val ProceedingJoinPoint.hasSuspendFunction: Boolean
        get() {
            val method = (this.signature as MethodSignature).method
            return KotlinDetector.isSuspendingFunction(method)
        }

}