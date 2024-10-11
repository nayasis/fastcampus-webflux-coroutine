package dev.fastcampus.mvc.repository

import dev.fastcampus.mvc.model.CompositePkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompositePkRepository: JpaRepository<CompositePkEntity, CompositePkEntity.pk>