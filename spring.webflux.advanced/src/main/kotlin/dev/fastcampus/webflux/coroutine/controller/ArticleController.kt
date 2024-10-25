package dev.fastcampus.webflux.coroutine.controller

import dev.fastcampus.webflux.coroutine.config.CacheManager
import dev.fastcampus.webflux.coroutine.model.Article
import dev.fastcampus.webflux.coroutine.service.ArticleService
import dev.fastcampus.webflux.coroutine.service.ReqCreate
import dev.fastcampus.webflux.coroutine.service.ReqUpdate
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class ArticleController(
    private val service: ArticleService,
    private val cache: CacheManager
) {

    @GetMapping("/article/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        val key = "/article/get:${id}"
        return cache.get(key) ?: run { service.get(id).also { cache.set(key, it) } }
    }

    @DeleteMapping("/evict/article/get")
    suspend fun evict() {
        cache.deleteAll("/article/get:")
    }

    @GetMapping("/article/all")
    suspend fun getAll(@RequestParam title: String?): Flow<Article> {
        return service.getAll(title)
    }

    @PostMapping("/article")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ReqCreate): Article {
        return service.create(request)
    }

    @PutMapping("/article/{id}")
    suspend fun update(@PathVariable id: Long, @RequestBody request: ReqUpdate): Article {
        return service.update(id, request)
    }

    @DeleteMapping("/article/{id}")
    suspend fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

}