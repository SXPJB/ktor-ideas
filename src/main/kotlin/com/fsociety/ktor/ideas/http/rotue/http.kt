package com.fsociety.ktor.ideas.http.rotue

import com.fsociety.ktor.ideas.http.controller.PersonController
import com.fsociety.ktor.ideas.http.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val personController: PersonController by inject()
    val userController: UserController by inject()
    routing {
        personRoute(personController)
        userRoute(userController)
        healthCheckRouting()
    }
}
