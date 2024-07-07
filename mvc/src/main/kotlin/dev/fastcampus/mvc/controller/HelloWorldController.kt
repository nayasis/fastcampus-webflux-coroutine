package dev.fastcampus.mvc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping("/")
    fun index(name: String?): String {
        return "Hello ${name ?: "MVC"}"
    }

}