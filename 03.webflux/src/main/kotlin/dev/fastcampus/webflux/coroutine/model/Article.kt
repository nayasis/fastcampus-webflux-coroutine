package dev.fastcampus.webflux.coroutine.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.annotation.processing.Generated

@Table("TB_ARTICLE")
class Article(
    @Id
    @Generated
    var id: Long = 0,

    var title: String = "",

    var body: String? = null,

    var authorId: Long? = null,

): BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Article(id=$id, title='$title', body=$body, authorId=$authorId, ${super.toString()})"
    }

}

