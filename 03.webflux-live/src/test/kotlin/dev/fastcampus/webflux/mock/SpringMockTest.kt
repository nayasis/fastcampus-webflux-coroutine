package dev.fastcampus.webflux.mock

import com.ninjasquad.springmockk.MockkBean
import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import dev.fastcampus.webflux.service.ArticleService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [
    ArticleService::class,
])
class SpringMockTest(
    @MockkBean
    private val repository: ArticleRepository,
    private val service: ArticleService,
): StringSpec({

    "get" {
        coEvery { repository.findById(1) } returns Article(id=1, title="async",body="mock")
        service.get(1).title shouldBe "async"
    }

})