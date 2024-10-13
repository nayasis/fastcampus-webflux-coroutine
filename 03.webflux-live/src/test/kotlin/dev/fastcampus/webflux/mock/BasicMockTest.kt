package dev.fastcampus.webflux.mock

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import dev.fastcampus.webflux.service.ArticleService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk

class BasicMockTest: StringSpec({

    val repository = mockk<ArticleRepository>()
    val service = ArticleService(repository)

    "get" {
        coEvery { repository.findById(1) } returns Article(id=1, title="async",body="mock")
        service.get(1).title shouldBe "async"
    }

    afterTest {
        clearAllMocks()
    }

})