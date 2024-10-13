package dev.fastcampus.webflux.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/hello")
    suspend fun index(request: ReqHello): String {
        return "Hello async ${request}"
    }

}

data class ReqHello(
    val name: String = "",
    val age: Int = 0,
)