package dev.fastcampus.webflux.coroutine.config

import dev.fastcampus.webflux.coroutine.config.extension.txid
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.context.ContextRegistry
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*
import kotlin.math.log

const val KEY_TXID = "txid"

private val logger = KotlinLogging.logger {}

@Component
@Order(1)
class TxidFilter: WebFilter {

    init {
        // maven micrometer context propagation
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(
            KEY_TXID, // key
            {MDC.get(KEY_TXID)}, // supplier
            {vaule -> MDC.put(KEY_TXID, vaule)}, // consumer
            {MDC.remove(KEY_TXID)}, // reset
        )
    }


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uuid = exchange.request.headers["x-txid"]?.firstOrNull() ?: "${UUID.randomUUID()}"
        MDC.put(KEY_TXID, uuid)
        return chain.filter(exchange).contextWrite { ctx ->
            ctx.put(KEY_TXID, uuid)
        }.doOnError {
            exchange.request.txid = uuid
        }.doFinally {
            MDC.remove(KEY_TXID)
        }
    }
}