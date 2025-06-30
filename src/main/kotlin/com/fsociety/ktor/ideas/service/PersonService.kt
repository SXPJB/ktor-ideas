package com.fsociety.ktor.ideas.service

import arrow.core.Either
import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.common.request.UpdatePersonRequest
import com.fsociety.ktor.ideas.common.response.ErrorApi
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository

class PersonService(
    private val personRepository: PersonRepository
) {
    suspend fun create(request: CreatePersonRequest): Person {
        val entity = request.toPerson()
        return personRepository.create(entity)
    }

    suspend fun update(id: Int, request: UpdatePersonRequest): Either<ErrorApi, Person> {
        val entity = request.toPerson()
        return personRepository.update(id, entity)
    }

    suspend fun findById(id: Int): Either<ErrorApi, Person> {
        return personRepository.findById(id)
    }

    suspend fun deleteById(id: Int): Either<ErrorApi, Unit> {
        return personRepository.deleteById(id)
    }

    suspend fun findAll(): List<Person> {
        return personRepository.findAll()
    }
}