package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import dev.fastcampus.webflux.repository.ArticleRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

val logger = KotlinLogging.logger {}

@SpringBootTest
class ArticleServiceTest(
    private val service: ArticleService,
    private val repository: ArticleRepository,
    private val txManager: TransactionalOperator,
): StringSpec({

    suspend fun <T> rollback(f: suspend (ReactiveTransaction) -> T): T {
        return txManager.executeAndAwait { tx ->
            tx.setRollbackOnly()
            f.invoke(tx)
        }
    }

    "create & get" {
        rollback {
            val req = ReqCreate("title 1", "blabla 1", 1234)
            val id  = service.create(req).id
            service.get(id).let {
                it.title shouldBe req.title
                it.body shouldBe req.body
                it.authorId shouldBe req.authorId
            }
        }
    }

    "get - not found" { rollback {
        shouldThrow<NotFoundException> { service.get(99999999) }
        logger.debug { runBlocking { ">> count: ${ repository.count()}" } }
    }}

     "get all" { rollback {
         service.create(ReqCreate("title1"))
         service.create(ReqCreate("title2"))
         service.create(ReqCreate("title matched"))

         service.getAll().count() shouldBe 3
         service.getAll("matched").count() shouldBe 1
         service.getAll("none").count() shouldBe 0
     }}

    "update" { rollback {
        val new = service.create(ReqCreate("title 1", "blabla 1", 1234))
        service.update(new.id, ReqUpdate(authorId = 9999))
        service.get(new.id).authorId shouldBe 9999
    }}

    "delete" { rollback {
        val prev = repository.count()
        val created = service.create(ReqCreate("title 1", "blabla 1", 1234))
        repository.count() shouldBe prev + 1
        service.delete(created.id)
        repository.count() shouldBe prev
    }}

})