package com.fsociety.ktor.ideas.domain.dao

import com.fsociety.ktor.ideas.domain.db.PersonEntity
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.model.Person.Companion.buildPerson
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PersonDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PersonDAO>(PersonEntity)

    var name by PersonEntity.name
    var lastName by PersonEntity.lastName
    var email by PersonEntity.email
    var createdBy by PersonEntity.createdBy
    var createdAt by PersonEntity.createdAt
    var updatedBy by PersonEntity.updatedBy
    var updatedAt by PersonEntity.updatedAt

    fun toPerson(): Person {
        return buildPerson {
            withId = id.value
            withName = name
            withLastName = lastName
            withEmail = email
            withCreatedBy = createdBy
            withCreatedAt = createdAt
            withUpdatedBy = updatedBy
            withUpdatedAt = updatedAt
        }
    }
}