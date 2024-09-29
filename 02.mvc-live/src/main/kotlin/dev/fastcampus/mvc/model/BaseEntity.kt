package dev.fastcampus.mvc.model

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @CreatedDate
    var createdat: LocalDateTime? = null,
    @LastModifiedDate
    var updatedat: LocalDateTime? = null,
) {
    override fun toString(): String {
        return "createdat=$createdat, updatedat=$updatedat"
    }
}