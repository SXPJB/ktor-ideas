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
import kotlinx.datetime.Clock

val appModule = module {
    single<Clock> { Clock.System }
    // Repository
    single<PersonRepository> { PersonRepositoryImpl(get()) }
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
