package dev.fastcampus.mvc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class MvcApplication

fun main(args: Array<String>) {
	runApplication<MvcApplication>(*args)
}
