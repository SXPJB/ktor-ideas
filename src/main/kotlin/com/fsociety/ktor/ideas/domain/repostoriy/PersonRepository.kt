package com.fsociety.ktor.ideas.domain.repostoriy

import arrow.core.Either
import com.fsociety.ktor.ideas.common.response.ErrorApi
import com.fsociety.ktor.ideas.domain.model.Person

interface PersonRepository {
    suspend fun create(entity: Person): Person
    suspend fun update(id: Int, entity: Person): Either<ErrorApi, Person>
    suspend fun findAll(): List<Person>
    suspend fun findById(id: Int): Either<ErrorApi, Person>
    suspend fun deleteById(id: Int): Either<ErrorApi, Unit>
}