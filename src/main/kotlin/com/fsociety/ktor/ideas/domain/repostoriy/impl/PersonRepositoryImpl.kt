package com.fsociety.ktor.ideas.domain.repostoriy.impl

import com.fsociety.ktor.ideas.domain.db.DatabaseConfig.dbQuery
import com.fsociety.ktor.ideas.domain.db.PersonEntity
import com.fsociety.ktor.ideas.domain.db.PersonEntity.id
import com.fsociety.ktor.ideas.domain.db.PersonEntity.createBy
import com.fsociety.ktor.ideas.domain.db.PersonEntity.createdAt
import com.fsociety.ktor.ideas.domain.db.PersonEntity.email
import com.fsociety.ktor.ideas.domain.db.PersonEntity.lastName
import com.fsociety.ktor.ideas.domain.db.PersonEntity.name
import com.fsociety.ktor.ideas.domain.db.PersonEntity.updatedAt
import com.fsociety.ktor.ideas.domain.db.PersonEntity.updatedBy
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll

class PersonRepositoryImpl : PersonRepository {
    override suspend fun create(entity: Person): Person {
        return dbQuery {
            val id = PersonEntity.insertAndGetId {
                it[name] = entity.name
                it[lastName] = entity.lastName
                it[email] = entity.email
                it[createBy] = entity.createdBy
            }.value
            entity.copy(id)
        }
    }

    override suspend fun findAll(): List<Person> {
        return dbQuery {
            PersonEntity.selectAll().map { it.toPerson() }
        }
    }

    private fun ResultRow.toPerson(): Person {
        return Person(
            id = this[id].value,
            name = this[name],
            lastName = this[lastName],
            email = this[email],
            createdBy = this[createBy],
            createdAt = this[createdAt],
            updatedBy = this[updatedBy],
            updatedAt = this[updatedAt],
        )
    }
}
