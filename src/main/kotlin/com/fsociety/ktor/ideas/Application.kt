package com.fsociety.ktor.ideas

import com.fsociety.ktor.ideas.domain.db.DatabaseConfig
import com.fsociety.ktor.ideas.http.rotue.configureRouting
import com.fsociety.ktor.ideas.plugins.configureKoin
import com.fsociety.ktor.ideas.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseConfig.init(environment)
    configurePlugins()
    configureRouting()
}

private fun Application.configurePlugins() {
    configureKoin()
    configureSerialization()
}
