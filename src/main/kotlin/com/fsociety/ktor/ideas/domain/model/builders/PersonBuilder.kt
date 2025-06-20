package com.fsociety.ktor.ideas.domain.model.builders

import com.fsociety.ktor.ideas.domain.model.Person
import kotlinx.datetime.LocalDateTime

class PersonBuilder {
    var withId: Int? = null
    var withName: String = ""
    var withLastName: String = ""
    var withEmail: String = ""
    var withCreatedBy: String = ""
    var withCreatedAt: LocalDateTime? = null
    var withUpdatedBy: String? = null
    var withUpdatedAt: LocalDateTime? = null

    fun build(): Person = Person(
        withId, withName, withLastName, withEmail, withCreatedBy, withCreatedAt, withUpdatedBy, withUpdatedAt
    )
}