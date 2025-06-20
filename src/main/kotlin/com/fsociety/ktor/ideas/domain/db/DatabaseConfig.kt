package com.fsociety.ktor.ideas.domain.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private const val DATABASE_INI_EVENT = "Database initialization started"

object DatabaseConfig {

    fun init(env: ApplicationEnvironment) {
        Database.connect(buildConnection(env))
        env.log.info(DATABASE_INI_EVENT)
        transaction {
            SchemaUtils.create(PersonEntity)
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(IO) {
            transaction { block() }
        }

    private fun buildConnection(env: ApplicationEnvironment): HikariDataSource {
        val config = env.toDBConfig()
        return HikariDataSource(config)
    }

    private fun ApplicationEnvironment.toDBConfig(): HikariConfig =
        HikariConfig().apply {
            jdbcUrl = config.property("database.jdbcUrl").getString()
            username = config.property("database.username").getString()
            password = config.property("database.password").getString()
            driverClassName = config.property("database.driverClassName").getString()
            maximumPoolSize = config.propertyOrNull("database.maxPoolSize")?.getString()?.toInt() ?: 3
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            isAutoCommit = false
            validate()
        }
}