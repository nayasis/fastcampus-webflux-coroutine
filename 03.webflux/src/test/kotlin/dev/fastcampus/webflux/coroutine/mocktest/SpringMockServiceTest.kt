package dev.fastcampus.webflux.coroutine.mocktest

import com.ninjasquad.springmockk.MockkBean
import dev.fastcampus.webflux.coroutine.exception.NotFoundException
import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import dev.fastcampus.webflux.coroutine.service.ArticleService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [
    ArticleService::class,
])
class SpringMockServiceTest(
    @MockkBean
    private val repository: ArticleRepository,
    private val service: ArticleService,
): StringSpec({

    "get" {
        coEvery { repository.findById(1) } returns Article(1, "merong", "mock")
        service.get(1).title shouldBe "merong"

        coEvery { repository.findById(any()) } returns null
        assertThrows<NotFoundException> { service.get(999).title }

    }

})