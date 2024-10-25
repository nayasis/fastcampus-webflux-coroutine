package dev.fastcampus.webflux.coroutine.service

import dev.fastcampus.webflux.coroutine.exception.NoAccountFound
import kotlinx.coroutines.delay
import dev.fastcampus.webflux.coroutine.model.Article as Account
import dev.fastcampus.webflux.coroutine.repository.ArticleRepository as AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.Duration.Companion.seconds

@Service
class AccountService(
    private val repository: AccountRepository,
) {

    suspend fun get(id: Long): ResAccount {
        return repository.findById(id)?.let { ResAccount(it) } ?: throw NoAccountFound("id: $id")
    }

    @Transactional
    suspend fun deposit(id: Long, amount: Long, delay: Int): ResAccount {
        return repository.findById(id)?.let {
            delay(delay.seconds)
            repository.save(it.apply {
                balance += amount
            }).let { ResAccount(it) }
        } ?: throw NoAccountFound("id: $id")
    }

    @Transactional
    suspend fun create(): ResAccount {
        return repository.save(Account(
            balance = 0,
            version = 0,
        )).let { ResAccount(it) }
    }

}

data class ResAccount(
    val id: Long,
    val balance: Long,
    var version: Int,
) {
    constructor(account: Account): this(
        id = account.id,
        balance = account.balance,
        version = account.version,
    )
}