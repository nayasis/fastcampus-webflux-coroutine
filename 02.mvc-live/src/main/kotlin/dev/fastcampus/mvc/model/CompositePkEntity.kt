package dev.fastcampus.mvc.model

import jakarta.persistence.Column
import jakarta.persistence.Id
import java.io.Serializable

class CompositePkEntity {

    @Id
    var id: String? = null

    @Id
    var type: String? = null

    @Column
    var value: String? = null

    data class pk (
        @Column(name="id")
        val id: String,
        @Column(name="type")
        val type: String,
    ): Serializable

}