package dev.fastcampus.webflux.coroutine.model

import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
): Serializable {
    override fun toString(): String = "createdAt=$createdAt, updatedAt=$updatedAt"
}