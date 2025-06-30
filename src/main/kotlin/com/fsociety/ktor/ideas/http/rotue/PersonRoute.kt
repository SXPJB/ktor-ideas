package com.fsociety.ktor.ideas.http.rotue

import arrow.core.Either
import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import com.fsociety.ktor.ideas.common.request.UpdatePersonRequest
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
            call.respond(HttpStatusCode.Created, result)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest, "Bad Request")

            val request = call.receive<UpdatePersonRequest>()
            val result = controller.updated(id, request)
            when (result) {
                is Either.Left -> call.respond(HttpStatusCode.BadRequest, result.value)
                is Either.Right -> call.respond(HttpStatusCode.OK, result.value)
            }
        }

        get("/all") {
            val result = controller.finaAll()
            call.respond(HttpStatusCode.OK, result)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest, "Bad Request")

            val result = controller.findById(id)
            when (result) {
                is Either.Left -> call.respond(HttpStatusCode.BadRequest, result.value)
                is Either.Right -> call.respond(HttpStatusCode.OK, result.value)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest, "Bad Request")

            val result = controller.delete(id)
            when (result) {
                is Either.Left -> call.respond(HttpStatusCode.BadRequest, result.value)
                is Either.Right -> call.respond(HttpStatusCode.NoContent, result.value)
            }
        }
    }
}