package dev.fastcampus.mvc.controller

import dev.fastcampus.mvc.model.Article
import dev.fastcampus.mvc.service.ArticleService
import dev.fastcampus.mvc.service.ReqCreate
import dev.fastcampus.mvc.service.ReqUpdate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/article")
class ArticleController(
    private val service: ArticleService,
) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): Article {
        return service.get(id)
    }

    @GetMapping("/all")
    fun getAll(@RequestParam title: String?): List<Article> {
        return service.getAll(title)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ReqCreate): Article {
        return service.create(request)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ReqUpdate): Article {
        return service.update(id, request)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }

}