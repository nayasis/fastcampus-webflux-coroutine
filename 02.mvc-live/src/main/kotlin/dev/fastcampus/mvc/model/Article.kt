package dev.fastcampus.mvc.model

import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity(name = "TB_ARTICLE")
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var title: String = "",
    var body: String = "",
    var authorId: Long? = null,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
//        other as Article
        return id == (other as Article).id
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Article(id=$id, title='$title', body='$body', ${super.toString()}"
    }
}