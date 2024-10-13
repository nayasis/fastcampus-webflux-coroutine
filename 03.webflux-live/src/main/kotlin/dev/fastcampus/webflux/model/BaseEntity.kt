package dev.fastcampus.webflux.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDateTime

open class BaseEntity(
    @CreatedDate
    var createdat: LocalDateTime? = null,
    @LastModifiedDate
    var updatedat: LocalDateTime? = null,
): Serializable {
    override fun toString(): String = "createdAt=$createdat, updatedAt=$updatedat"
}