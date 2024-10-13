package dev.fastcampus.webflux.service

import dev.fastcampus.webflux.exception.NotFoundException
import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val repository: ArticleRepository,
) {
    suspend fun get(id: Long): Article {
        return repository.findById(id) ?: throw NotFoundException("article id: $id")
    }

    suspend fun getAll(title: String? = null): Flow<Article> {
        return if(title.isNullOrEmpty()) {
            repository.findAll()
        } else {
            repository.findAllByTitleContains(title)
        }
    }

    @Transactional
    suspend fun create(request: ReqCreate): Article {
        return repository.save(Article(
            title = request.title,
            body = request.body,
            authorId = request.authorId
        ))
    }

    @Transactional
    suspend fun update(id: Long, request: ReqUpdate): Article {
        return repository.findById(id)?.let { article ->
            request.title?.let { article.title = it }
            request.body?.let { article.body = it }
            request.authorId?.let { article.authorId = it }
            repository.save(article)
        } ?: throw NotFoundException("article id: $id")
    }

    @Transactional
    suspend fun delete(id: Long) {
        repository.findById(id)?.let { article ->
            repository.delete(article)
        } ?: throw NotFoundException("article id: $id")
    }

}

data class ReqUpdate(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ReqCreate(
    val title: String,
    val body: String? = null,
    val authorId: Long? = null,
)
