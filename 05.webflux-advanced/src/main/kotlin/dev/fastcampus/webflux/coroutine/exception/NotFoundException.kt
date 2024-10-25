package dev.fastcampus.webflux.coroutine.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String? = null, cause: Throwable? = null): Throwable(message, cause)
