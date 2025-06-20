package com.fsociety.ktor.ideas.http.controller

import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.service.PersonService

class PersonController(
    private val personService: PersonService,
) {

    suspend fun create(request: CreatePersonRequest): Person {
        return personService.create(request)
    }

    suspend fun finaAll(): List<Person> {
        return personService.findAll()
    }
}