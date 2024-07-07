package dev.fastcampus.webflux.coroutine

import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests(
	private val repository: ArticleRepository,
): StringSpec({

	"contest load" {
		val count = repository.count()
		count shouldBeGreaterThanOrEqual 0
	}

})