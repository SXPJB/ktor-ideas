package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.common.utils.createProducer
import com.fsociety.ktor.ideas.domain.repostoriy.PersonRepository
import com.fsociety.ktor.ideas.domain.repostoriy.impl.PersonRepositoryImpl
import com.fsociety.ktor.ideas.http.controller.PersonController
import com.fsociety.ktor.ideas.http.controller.UserController
import com.fsociety.ktor.ideas.service.PersonService
import com.fsociety.ktor.ideas.service.UserService
import com.fsociety.ktor.ideas.streaming.KafkaProducer
import io.ktor.server.application.*
import kotlinx.datetime.Clock
import org.apache.kafka.clients.producer.Producer
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    single<Clock> { Clock.System }

    // Kafka Configuration
    single<String>(qualifier = named("userTopic")) { "user-topic" }

    // Kafka Producer
    single<Producer<String, User>> {
        createProducer(
            // TODO: Create it Configuration for KtorKafkaPlugin
            bootstrapServers = "localhost:9093",
            serializer = User.UserSerializer()
        )
    }

    single<KafkaProducer> {
        KafkaProducer(
            userProProducer = get(),
            userTopic = get(qualifier = named("userTopic"))
        )
    }

    // Repository
    single<PersonRepository> { PersonRepositoryImpl(get()) }
    // Service
    singleOf(::PersonService)
    singleOf(::UserService)
    // Controller
    singleOf(::PersonController)
    singleOf(::UserController)
}

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
