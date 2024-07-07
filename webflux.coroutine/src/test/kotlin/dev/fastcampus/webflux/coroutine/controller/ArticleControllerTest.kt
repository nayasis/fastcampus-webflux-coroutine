package dev.fastcampus.webflux.coroutine.controller

import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import dev.fastcampus.webflux.coroutine.service.ArticleService
import dev.fastcampus.webflux.coroutine.service.ReqCreate
import dev.fastcampus.webflux.coroutine.service.ReqUpdate
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@Rollback
@AutoConfigureWebTestClient
class ArticleControllerTest(
    private val client: WebTestClient,
    private val service: ArticleService,
    private val repository: ArticleRepository,
): StringSpec({

    suspend fun getAllCount() =
        client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.asFlow().count()

    beforeTest {
        repository.deleteAll()
    }

    "create & get" {
        val request = ReqCreate("test", "r2dbc demo", 1234)
        val newId   = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .returnResult(Article::class.java)
            .responseBody.awaitSingle().id

        val created = client.get().uri("/article/${newId}").accept(APPLICATION_JSON)
            .exchange().expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.awaitSingle()

        request.title shouldBe created.title
        request.body shouldBe created.body
        request.authorId shouldBe created.authorId
    }

    "get: not found" {
        client.get().uri("/article/99999999").accept(APPLICATION_JSON).exchange()
        .expectStatus().isNotFound
    }

    "get all" {

        service.create(ReqCreate("title1"))
        service.create(ReqCreate("title2"))
        service.create(ReqCreate("title matched"))

        getAllCount() shouldBe 3

        client.get().uri("/article/all?title=matched").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.asFlow().count() shouldBe 1

        client.get().uri("/article/all?title=none").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.asFlow().count() shouldBe 0
    }

    "update" {

        val new = service.create(ReqCreate("title 1", "blabla 1", 1234))

        client.put().uri("/article/${new.id}").bodyValue(ReqUpdate(authorId = 9999)).accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)

        val updated = client.get().uri("/article/${new.id}").accept(APPLICATION_JSON)
            .exchange().expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.awaitSingle()

        updated.authorId shouldBe 9999

    }

    "delete" {
        val prev = getAllCount()

        val created = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("test", "r2dbc demo", 1234))
            .exchange()
            .expectStatus().isCreated
            .returnResult(Article::class.java)
            .responseBody.awaitSingle()

        getAllCount() shouldBe prev + 1

        client.delete().uri("/article/${created.id}")
            .accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk

        getAllCount() shouldBe prev

    }

})


