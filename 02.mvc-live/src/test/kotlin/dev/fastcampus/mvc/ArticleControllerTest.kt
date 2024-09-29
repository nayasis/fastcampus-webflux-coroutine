package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.service.ArticleService
import dev.fastcampus.mvc.service.ReqCreate
import dev.fastcampus.mvc.service.ReqUpdate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class ArticleControllerTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val service: ArticleService,
) {
    @Test
    fun `create & get`() {
        val request = ReqCreate("test", "r2dbc demo", 1234)
        val newId   = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .returnResult(Article::class.java)
            .responseBody.blockFirst()!!.id

        val created = client.get().uri("/article/${newId}").accept(APPLICATION_JSON)
            .exchange().expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.blockFirst()!!

        assertEquals(created.title, created.title)
        assertEquals(created.body, created.body)
        assertEquals(created.authorId, created.authorId)
    }

    @Test
    fun `get - not found`() {
        client.get().uri("/article/99999999").accept(APPLICATION_JSON).exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get all`() {

        service.create(ReqCreate("title1", body = ""))
        service.create(ReqCreate("title2", body = ""))
        service.create(ReqCreate("title matched", body = ""))

        assertEquals( 3, client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.count().block()
        )

        assertEquals( 1, client.get().uri("/article/all?title=matched").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.count().block()
        )

        assertEquals( 0, client.get().uri("/article/all?title=none").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.count().block()
        )

    }

    @Test
    fun `update`() {

        val new = service.create(ReqCreate("title 1", "blabla 1", 1234))

        client.put().uri("/article/${new.id}").accept(APPLICATION_JSON)
            .bodyValue(ReqUpdate(authorId = 9999)).exchange()
            .expectStatus().isOk

        val updated = client.get().uri("/article/${new.id}").accept(APPLICATION_JSON)
            .exchange().expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.blockFirst()!!

        assertEquals(9999, updated.authorId)
    }

    @Test
    fun `delete`() {

        val prev = getAllCount()

        val created = client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(ReqCreate("test", "r2dbc demo", 1234)).exchange()
            .expectStatus().isCreated
            .returnResult(Article::class.java)
            .responseBody.blockFirst()!!

        assertEquals(prev + 1, getAllCount())

        client.delete().uri("/article/${created.id}").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk

        assertEquals(prev, getAllCount())

    }

    fun getAllCount(): Long {
        return client.get().uri("/article/all").accept(APPLICATION_JSON).exchange()
            .expectStatus().isOk
            .returnResult(Article::class.java)
            .responseBody.count().block() ?: 0
    }

}