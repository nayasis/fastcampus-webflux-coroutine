package dev.fastcampus.mvc.service

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.repository.ArticleRepository
import dev.fastcampus.mvc.service.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val repository: ArticleRepository
) {

    fun get(id: Long): Article {
        return repository.findByIdOrNull(id) ?: throw NotFoundException("aritlcle id: $id")
    }

    fun getAll(title: String? = null): List<Article> {
        return if(title.isNullOrEmpty()) {
            repository.findAll()
        } else {
            repository.findAllByTitleContains(title)
        }
    }

    @Transactional
    fun create(request: ReqCreate): Article {
        return repository.save(Article(
            title = request.title,
            body = request.body,
            authorId = request.authorId,
        ))
    }

    @Transactional
    fun update(id: Long, request: ReqUpdate): Article {
        return repository.findByIdOrNull(id)?.let {article ->
            request.title?.let { article.title = it }
            request.body?.let { article.body = it }
            request.authorId?.let { article.authorId = it }
            repository.save(article)
        } ?: throw NotFoundException("aritlcle id: $id")
    }

    @Transactional
    fun delete(id: Long) {
        repository.findByIdOrNull(id)?.let { article ->
            repository.delete(article)
        } ?: throw NotFoundException("aritlcle id: $id")
    }


}

data class ReqUpdate(
    val title: String? = null,
    val body: String? = null,
    val authorId: Long? = null,
)

data class ReqCreate(
    val title: String,
    val body: String,
    val authorId: Long? = null,
)