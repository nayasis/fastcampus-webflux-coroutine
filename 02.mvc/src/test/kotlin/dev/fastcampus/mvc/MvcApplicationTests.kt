package dev.fastcampus.mvc

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.repository.ArticleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private val logger = KotlinLogging.logger {}

@SpringBootTest
class MvcApplicationTests(
	@Autowired private val repository: ArticleRepository
) {

	@Test
	fun contextLoads() {
		val created = repository.save(Article(title = "test", body = "blabla", authorId = 1234))
		assertTrue { repository.count() > 0 }

		logger.debug { created }

		assertTrue { created.id > 0 }
		assertNotNull(created.createdAt)
		assertNotNull(created.updatedAt)
	}

}