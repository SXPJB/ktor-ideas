package com.fsociety.ktor.ideas.domain.repostoriy

import com.fsociety.ktor.ideas.domain.model.Person

interface PersonRepository {
    suspend fun create(entity: Person): Person
    suspend fun findAll(): List<Person>
}