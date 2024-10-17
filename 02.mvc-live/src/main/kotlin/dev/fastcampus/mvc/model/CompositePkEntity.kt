package dev.fastcampus.mvc.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

@Entity(name = "TB_COMPOSITE_KEY")
@IdClass(CompositePkEntity.pk::class)
class CompositePkEntity {

    @Id
    @Column(name="pk_id")
    var id: String? = null

    @Id
    @Column(name="pk_type")
    var type: String? = null

    @Column(name="value")
    var value: String? = null

    data class pk (
        val id: String,
        val type: String,
    ): Serializable

}