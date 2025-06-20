package com.fsociety.ktor.ideas.http.controller

import arrow.core.Either
import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.common.request.UpdatePersonRequest
import com.fsociety.ktor.ideas.common.response.ErrorApi
import com.fsociety.ktor.ideas.common.response.PersonApi
import com.fsociety.ktor.ideas.service.PersonService

class PersonController(
    private val personService: PersonService,
) {
    suspend fun create(request: CreatePersonRequest): PersonApi {
        return personService.create(request).toApi()
    }

    suspend fun updated(id: Int, request: UpdatePersonRequest): Either<ErrorApi, PersonApi> {
        return personService.update(id, request).map { it.toApi() }
    }

    suspend fun findById(id: Int): Either<ErrorApi, PersonApi> {
        return personService.findById(id).map { it.toApi() }
    }

    suspend fun delete(id: Int): Either<ErrorApi, Unit> {
        return personService.deleteById(id)
    }

    suspend fun finaAll(): List<PersonApi> {
        return personService.findAll().map { it.toApi() }
    }
}