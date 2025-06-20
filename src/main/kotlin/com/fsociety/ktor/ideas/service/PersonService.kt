package com.fsociety.ktor.ideas.service

import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository

class PersonService(
    private val personRepository: PersonRepository
) {
    suspend fun create(request: CreatePersonRequest): Person {
        val entity = request.toPerson()
        return personRepository.create(entity)
    }

    suspend fun findAll(): List<Person> {
        return personRepository.findAll()
    }
}