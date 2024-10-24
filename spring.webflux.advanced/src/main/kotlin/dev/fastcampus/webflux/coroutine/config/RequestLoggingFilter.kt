package dev.fastcampus.webflux.coroutine.config

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

//@Component
//@Order(11)
//class RequestLoggingFilter: WebFilter {
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        TODO("Not yet implemented")
//    }
//}