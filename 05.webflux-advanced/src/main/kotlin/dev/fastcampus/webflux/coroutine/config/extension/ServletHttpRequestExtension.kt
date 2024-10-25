package dev.fastcampus.webflux.coroutine.config.extension

import org.springframework.http.server.reactive.ServerHttpRequest

private val txidByReqid = HashMap<String,String>()

var ServerHttpRequest.txid: String?
    get() {
        return txidByReqid[this.id]
    }
    set(value) {
        if(value == null) {
            txidByReqid.remove(this.id)
        } else {
            txidByReqid[this.id] = value
        }
    }