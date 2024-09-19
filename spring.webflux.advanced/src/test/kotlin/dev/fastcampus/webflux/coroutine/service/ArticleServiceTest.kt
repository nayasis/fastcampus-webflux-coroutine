package dev.fastcampus.webflux.coroutine.service

import dev.fastcampus.webflux.coroutine.exception.NotFoundException
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

private val logger = KotlinLogging.logger {}

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    private val service: ArticleService,
    private val repository: ArticleRepository,
    private val tx: TransactionalOperator,
): StringSpec({

    "create & get" { tx.rollback {
        val req = ReqCreate("title 1", "blabla 1", 1234)
        val id  = service.create(req).id
        service.get(id).let {
            it.title shouldBe req.title
            it.body shouldBe req.body
            it.authorId shouldBe req.authorId
        }
        logger.debug { ">> count: ${runBlocking {repository.count()}}" }
    }}

    "get: not found" {
        shouldThrow<NotFoundException> { service.get(99999999) }
    }

    "get all" { tx.rollback {
        service.create(ReqCreate("title1"))
        service.create(ReqCreate("title2"))
        service.create(ReqCreate("title matched"))

        service.getAll().count() shouldBe 3
        service.getAll("matched").count() shouldBe 1
        service.getAll("none").count() shouldBe 0

        logger.debug { ">> count: ${runBlocking {repository.count()}}" }

    }}

    "update" { tx.rollback {
        val new = service.create(ReqCreate("title 1", "blabla 1", 1234))
        service.update(new.id, ReqUpdate(authorId = 9999))
        service.get(new.id).authorId shouldBe 9999

        logger.debug { ">> count: ${runBlocking {repository.count()}}" }
    }}

    "delete" { tx.rollback {
        val prev = repository.count()
        val created = service.create(ReqCreate("title 1", "blabla 1", 1234))
        repository.count() shouldBe prev + 1
        service.delete(created.id)
        repository.count() shouldBe prev

        logger.debug { ">> count: ${runBlocking {repository.count()}}" }
    }}

})


suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T): T {
    return executeAndAwait { tx ->
        tx.setRollbackOnly()
        f.invoke(tx)
    }
}