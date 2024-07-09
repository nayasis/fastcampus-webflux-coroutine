package dev.fastcampus.mvc.service

import dev.fastcampus.mvc.exception.NotFoundException
import dev.fastcampus.mvc.repository.ArticleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
) {

    @Test
    fun `create & get`() {
        val req = ReqCreate("title 1", "blabla 1", 1234)
        val id  = service.create(req).id
        service.get(id).let {
            assertEquals(req.title, it.title)
            assertEquals(req.body, it.body)
            assertEquals(req.authorId, it.authorId)
        }
        logger.debug { ">> count: ${repository.count()}" }
    }

    @Test
    fun `get - not found`() {
        assertThrows<NotFoundException> { service.get(99999999)  }
        logger.debug { ">> count: ${repository.count()}" }
    }

    @Test
    fun getAll() {
        service.create(ReqCreate("title1"))
        service.create(ReqCreate("title2"))
        service.create(ReqCreate("title matched"))

        assertEquals(3, service.getAll().size)
        assertEquals(1, service.getAll("matched").size)
        assertEquals(0, service.getAll("none").size)

        logger.debug { ">> count: ${repository.count()}" }
    }

    @Test
    fun update() {
        val new = service.create(ReqCreate("title 1", "blabla 1", 1234))
        service.update(new.id, ReqUpdate(authorId = 9999))
        assertEquals(9999, service.get(new.id).authorId)
    }

    @Test
    fun delete() {
        val prev = repository.count()
        val created = service.create(ReqCreate("title 1", "blabla 1", 1234))
        assertEquals(prev + 1, repository.count())
        service.delete(created.id)
        assertEquals(prev, repository.count())

        logger.debug { ">> count: ${repository.count()}" }
    }

}