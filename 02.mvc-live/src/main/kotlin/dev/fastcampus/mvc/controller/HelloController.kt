package dev.fastcampus.mvc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    fun inder(): String {
        return "Hello mvc"
    }

}