package com.fsociety.ktor.ideas.common.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PersonApi(
    val id: Int? = null,
    val name: String,
    val lastName: String,
    val email: String,
    val createdBy: String,
    val createdAt: LocalDateTime? = null,
    val updatedBy: String? = null,
    val updatedAt: LocalDateTime? = null,
)