package com.fsociety.ktor.ideas.domain.repostoriy.impl

import arrow.core.Either
import arrow.core.Option
import com.fsociety.ktor.ideas.common.response.ErrorApi
import com.fsociety.ktor.ideas.common.utils.logger
import com.fsociety.ktor.ideas.domain.dao.PersonDAO
import com.fsociety.ktor.ideas.domain.db.DatabaseConfig.dbQuery
import com.fsociety.ktor.ideas.domain.model.Person
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PersonRepositoryImpl(
    private val clock: Clock,
) : PersonRepository {

    companion object {
        private val log = logger()
    }

    override suspend fun create(entity: Person): Person = dbQuery {
        PersonDAO.new {
            name = entity.name
            lastName = entity.lastName
            email = entity.email
            createdBy = entity.createdBy
        }.toPerson()
    }

    override suspend fun update(id: Int, entity: Person): Either<ErrorApi, Person> {
        return findPersonDaoById(id)
            .map { personDAO ->
                dbQuery {
                    personDAO.apply {
                        name = entity.name
                        lastName = entity.lastName
                        email = entity.email
                        updatedBy = entity.updatedBy
                        updatedAt = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    }
                }
            }
            .map(PersonDAO::toPerson)
    }

    override suspend fun findAll(): List<Person> = dbQuery {
        PersonDAO.all().map(PersonDAO::toPerson)
    }

    override suspend fun findById(id: Int): Either<ErrorApi, Person> {
        return findPersonDaoById(id).map(PersonDAO::toPerson)
    }

    override suspend fun deleteById(id: Int): Either<ErrorApi, Unit> {
        return findPersonDaoById(id).map {
            dbQuery {
                it.delete()
            }
        }
    }

    private suspend fun findPersonDaoById(id: Int): Either<ErrorApi, PersonDAO> = dbQuery {
        Option.fromNullable(PersonDAO.findById(id))
            .toEither {
                log.debug("Person is not found by id $id")
                ErrorApi.from("Person not found: $id", HttpStatusCode.NotFound)
            }
    }
}