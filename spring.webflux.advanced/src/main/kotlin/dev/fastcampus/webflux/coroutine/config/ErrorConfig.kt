package dev.fastcampus.webflux.coroutine.config

import dev.fastcampus.webflux.coroutine.config.extension.txid
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest

private val logger = KotlinLogging.logger {}

@Configuration
class ErrorConfig {
    @Bean
    fun errorAttribute(): DefaultErrorAttributes {
        return object: DefaultErrorAttributes() {
            override fun getErrorAttributes(
                serverRequest: ServerRequest?,
                options: ErrorAttributeOptions?,
            ): MutableMap<String, Any> {

//                return super.getErrorAttributes(serverRequest, options)

                val request = serverRequest?.exchange()?.request ?: return mutableMapOf()
                val txid = request.txid ?: ""

                MDC.put(KEY_TXID, txid)

                try {
                    if(logger.isErrorEnabled()) {
                        super.getError(serverRequest).let { e ->
                            logger.error(e) { e.message ?: "Internal Server Error" }
                        }
                    }
                    return super.getErrorAttributes(serverRequest, options).apply {
                        remove("requestId")
                        put(KEY_TXID, txid)
                    }
                } finally {
                    request.txid = null
                    MDC.remove(KEY_TXID)
                }

            }
        }
    }
}