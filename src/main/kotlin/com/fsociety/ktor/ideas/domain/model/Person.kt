package com.fsociety.ktor.ideas.domain.model

import com.fsociety.ktor.ideas.common.response.PersonApi
import kotlinx.datetime.LocalDateTime

data class Person(
    val id: Int? = null,
    val name: String,
    val lastName: String,
    val email: String,
    val createdBy: String,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
) {
    fun toApi(): PersonApi {
        return PersonApi(
            id = id,
            name = name,
            lastName = lastName,
            email = email,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
        )
    }
}
