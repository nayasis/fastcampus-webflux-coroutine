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

@_root_ide_package_.org.springframework.boot.test.context.SpringBootTest
@_root_ide_package_.org.springframework.transaction.annotation.Transactional
@_root_ide_package_.org.springframework.test.annotation.Rollback
@_root_ide_package_.org.springframework.test.context.ActiveProfiles("test")
class ArticleServiceTest(
    @_root_ide_package_.org.springframework.beans.factory.annotation.Autowired private val service: _root_ide_package_.dev.fastcampus.mvc.service.ArticleService,
    @_root_ide_package_.org.springframework.beans.factory.annotation.Autowired private val repository: _root_ide_package_.dev.fastcampus.mvc.repository.ArticleRepository,
) {

    @_root_ide_package_.org.junit.jupiter.api.Test
    fun `create & get`() {
        val req = _root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title 1", "blabla 1", 1234)
        val id  = service.create(req).id
        service.get(id).let {
            _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(req.title, it.title)
            _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(req.body, it.body)
            _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(req.authorId, it.authorId)
        }
        _root_ide_package_.dev.fastcampus.mvc.service.logger.debug { ">> count: ${repository.count()}" }
    }

    @_root_ide_package_.org.junit.jupiter.api.Test
    fun `get - not found`() {
        _root_ide_package_.org.junit.jupiter.api.assertThrows<_root_ide_package_.dev.fastcampus.mvc.exception.NotFoundException> { service.get(99999999)  }
        _root_ide_package_.dev.fastcampus.mvc.service.logger.debug { ">> count: ${repository.count()}" }
    }

    @_root_ide_package_.org.junit.jupiter.api.Test
    fun getAll() {
        service.create(_root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title1"))
        service.create(_root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title2"))
        service.create(_root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title matched"))

        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(3, service.getAll().size)
        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(1, service.getAll("matched").size)
        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(0, service.getAll("none").size)

        _root_ide_package_.dev.fastcampus.mvc.service.logger.debug { ">> count: ${repository.count()}" }
    }

    @_root_ide_package_.org.junit.jupiter.api.Test
    fun update() {
        val new = service.create(_root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title 1", "blabla 1", 1234))
        service.update(new.id, _root_ide_package_.dev.fastcampus.mvc.service.ReqUpdate(authorId = 9999))
        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(9999, service.get(new.id).authorId)
    }

    @_root_ide_package_.org.junit.jupiter.api.Test
    fun delete() {
        val prev = repository.count()
        val created = service.create(_root_ide_package_.dev.fastcampus.mvc.service.ReqCreate("title 1", "blabla 1", 1234))
        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(prev + 1, repository.count())
        service.delete(created.id)
        _root_ide_package_.org.junit.jupiter.api.Assertions.assertEquals(prev, repository.count())

        _root_ide_package_.dev.fastcampus.mvc.service.logger.debug { ">> count: ${repository.count()}" }
    }

}