package dev.fastcampus.webflux

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class WebfluxApplicationTests(
	private val repository: ArticleRepository,
): StringSpec({

	"context load" {

		val prevCnt = repository.count()

		val created = repository.save(Article(
			title = "test",
			body = "blabla",
			authorId = 1234
		))

		repository.count() shouldBe prevCnt + 1

		val saved = repository.findById(created.id)
		saved?.createdat shouldNotBe null
		saved?.updatedat shouldNotBe null

	}

})
