package dev.fastcampus.webflux.coroutine.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping("/")
    fun index(@RequestParam name: String?): String {
        return "Hello ${name ?: "Coroutine"}"
    }
}