package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository
import com.fsociety.ktor.ideas.domain.repostoriy.impl.PersonRepositoryImpl
import com.fsociety.ktor.ideas.http.controller.PersonController
import com.fsociety.ktor.ideas.service.PersonService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    // Repository
    single<PersonRepository> { PersonRepositoryImpl() }
    // Service
    singleOf(::PersonService)
    // Controller
    singleOf(::PersonController)
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
