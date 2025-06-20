package com.fsociety.ktor.ideas.common.request

import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.model.Person.Companion.buildPerson
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
        return buildPerson {
            withName = name
            withLastName = lastName
            withEmail = email
            withCreatedBy = createdBy
        }
    }

}

@Serializable
data class UpdatePersonRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val updatedBy: String,
) {
    companion object

    fun toPerson(): Person {
        return buildPerson {
            withName = name
            withLastName = lastName
            withEmail = email
            withUpdatedBy = updatedBy
        }
    }
}
