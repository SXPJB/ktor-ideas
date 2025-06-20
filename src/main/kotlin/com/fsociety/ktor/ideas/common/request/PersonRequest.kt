package com.fsociety.ktor.ideas.common.request

import com.fsociety.ktor.ideas.domain.model.Person
import kotlinx.serialization.Serializable

@Serializable
data class CreatePersonRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val createdBy: String,
) {
    companion object

    fun toPerson(): Person {
        return Person(
            name = name,
            lastName = lastName,
            email = email,
            createdBy = createdBy,
        )
    }

}

