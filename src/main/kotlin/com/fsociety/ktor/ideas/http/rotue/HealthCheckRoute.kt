package com.fsociety.ktor.ideas.http.rotue


import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthCheckRouting() {
    get("/healthcheck") {
        call.respond(mapOf("status" to "OK"))
    }
}