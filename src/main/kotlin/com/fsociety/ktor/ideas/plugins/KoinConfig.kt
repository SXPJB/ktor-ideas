package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository
import com.fsociety.ktor.ideas.domain.repostoriy.impl.PersonRepositoryImpl
import com.fsociety.ktor.ideas.http.controller.PersonController
import com.fsociety.ktor.ideas.http.controller.UserController
import com.fsociety.ktor.ideas.infraestructure.kafka.common.getKafkaProducer
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer
import com.fsociety.ktor.ideas.service.PersonService
import com.fsociety.ktor.ideas.service.UserService
import io.ktor.server.application.*
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val appModule = module {
        single<Clock> { Clock.System }

        // Repository
        single<PersonRepository> { PersonRepositoryImpl(get()) }
        // Service
        singleOf(::PersonService)
        // Controller
        singleOf(::PersonController)
        singleOf(::UserController)
    }

    // Create a module for the Kafka producer
    val kafkaModule = module {
        single<KtorKafkaProducer<User>>(named("userProducer")) {
            getKafkaProducer("user-producer")
        }

        single<KtorKafkaProducer<String>>(named("stringProducer")) {
            getKafkaProducer("string-serializer")
        }

        single {
            UserService(
                get(named("userProducer")),
                get(named("stringProducer"))
            )
        }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule, kafkaModule)
    }
}
