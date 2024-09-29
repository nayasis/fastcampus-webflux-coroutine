package dev.fastcampus.mvc.service

import dev.fastcampus.mvc.repository.ArticleRepository
import dev.fastcampus.mvc.service.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@Transactional
@Rollback
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
) {

    @Test
    fun `create & get `() {

        val article = service.get(999)
        logger.debug { ">> article pre-defined: $article" }

        val req = ReqCreate("title 1", "blabl1 1", 1234)
        val id = service.create(req).id
        service.get(id).let {
            assertEquals(req.title, it.title)
            assertEquals(req.body, it.body)
            assertEquals(req.authorId, it.authorId)
        }
        logger.debug { ">> count: ${repository.count()}" }
    }

    @Test
    fun `get - not found`() {
        assertThrows<NotFoundException> { service.get(99999999) }
    }

    @Test
    fun getAll() {
        service.create(ReqCreate("title1", "blabl1 1", 1234))
        service.create(ReqCreate("title2", "blabl1 1", 1234))
        service.create(ReqCreate("title matched", "blabl1 1", 1234))

        assertEquals(3 , service.getAll().size)
        assertEquals(1 , service.getAll("matched").size)
        assertEquals(0 , service.getAll("none").size)
    }

    @Test
    fun update() {
        val req = ReqCreate("title 1", "blabl1 1", 1234)
        val new = service.create(req)

        service.update(new.id, ReqUpdate(title = "updated"))

        assertEquals("updated", service.get(new.id).title)
    }

    @Test
    fun delete() {
        val prevCnt = repository.count()
        val created = service.create(ReqCreate("title 1", "blabl1 1", 1234))
        assertEquals( prevCnt + 1, repository.count() )
        service.delete(created.id)
        assertEquals( prevCnt, repository.count() )
    }

}