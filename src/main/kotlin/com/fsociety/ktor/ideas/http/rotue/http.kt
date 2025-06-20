package com.fsociety.ktor.ideas.http.rotue

import com.fsociety.ktor.ideas.http.controller.PersonController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val personController: PersonController by inject()

    routing {
        userRoute(personController)
        healthCheckRouting()
    }
}
