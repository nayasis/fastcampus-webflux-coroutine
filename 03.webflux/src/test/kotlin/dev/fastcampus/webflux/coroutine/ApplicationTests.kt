package dev.fastcampus.webflux.coroutine

import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests(
	private val repository: ArticleRepository,
): StringSpec({

	"contest load" {

		val created = repository.save(Article(title = "test", body = "blabla", authorId = 1234))

		repository.count() shouldBeGreaterThan 0

		created.id shouldBeGreaterThan 0
		created.createdAt shouldNotBe null
		created.updatedAt shouldNotBe null

	}

})