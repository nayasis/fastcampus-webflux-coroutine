package dev.fastcampus.webflux.coroutine.controller

import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.service.ArticleService
import dev.fastcampus.webflux.coroutine.service.ReqCreate
import dev.fastcampus.webflux.coroutine.service.ReqUpdate
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/article")
class ArticleController(
    private val service: ArticleService
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        return service.get(id)
    }

    @GetMapping("/all")
    suspend fun getAll(@RequestParam title: String?): Flow<Article> {
        return service.getAll(title)
    }

    @PostMapping
    suspend fun create(request: ReqCreate): Article {
        return service.create(request)
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: Long, request: ReqUpdate): Article {
        return service.update(id, request)
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

}