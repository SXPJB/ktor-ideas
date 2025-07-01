package com.fsociety.ktor.ideas.http.rotue

import arrow.core.Either
import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.http.controller.UserController
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Route for handling Kafka User-related requests.
 */
fun Route.userRoute(
    controller: UserController
) {
    route("/user") {
        post<User> { user ->
            val result = controller.sendUser(user)
            when (result) {
                is Either.Left -> call.respond(HttpStatusCode.BadRequest, result.value)
                is Either.Right -> call.respond(HttpStatusCode.OK, result.value)
            }
        }
    }
}