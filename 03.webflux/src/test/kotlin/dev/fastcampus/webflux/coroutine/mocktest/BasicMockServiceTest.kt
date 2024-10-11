package dev.fastcampus.webflux.coroutine.mocktest

import dev.fastcampus.webflux.coroutine.exception.NotFoundException
import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import dev.fastcampus.webflux.coroutine.service.ArticleService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows

class BasicMockServiceTest: StringSpec({

    val repository = mockk<ArticleRepository>()

    val service = ArticleService(repository)

    "get" {
        coEvery { repository.findById(1) } returns Article(1, "merong", "mock")
        service.get(1).title shouldBe "merong"

        coEvery { repository.findById(any()) } returns null
        assertThrows<NotFoundException> { service.get(999).title }

    }

//    afterTest {
//        clearAllMocks()
//    }

})