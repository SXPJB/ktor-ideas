package com.fsociety.ktor.ideas.http.rotue

import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.http.controller.PersonController
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoute(
    controller: PersonController,
) {
    route("/user") {
        get("/") {
            call.respondText("User Management API")
        }

        post {
            val request = call.receive<CreatePersonRequest>()
            val result = controller.create(request)
            call.respond(HttpStatusCode.Created, result.toApi())
        }

        get("/all") {
            val result = controller.finaAll().map { it.toApi() }
            call.respond(HttpStatusCode.OK, result)
        }
    }
}