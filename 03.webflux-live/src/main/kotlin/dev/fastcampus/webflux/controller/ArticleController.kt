package dev.fastcampus.webflux.controller

import dev.fastcampus.webflux.model.Article
import dev.fastcampus.webflux.service.ArticleService
import dev.fastcampus.webflux.service.ReqCreate
import dev.fastcampus.webflux.service.ReqUpdate
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(
    private val service: ArticleService,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        return service.get(id)
    }

    @GetMapping("/all")
    suspend fun getAll(@RequestParam title: String?): Flow<Article> {
        return service.getAll(title)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: ReqCreate): Article {
        return service.create(request)
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: Long, @RequestBody request: ReqUpdate): Article {
        return service.update(id, request)
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

}