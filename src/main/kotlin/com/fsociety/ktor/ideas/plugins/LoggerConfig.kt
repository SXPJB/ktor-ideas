package com.fsociety.ktor.ideas.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.util.logging.*
import org.slf4j.event.Level.INFO

internal val logger = KtorSimpleLogger("com.fsociety.ktor.ideas.plugins.RequestTracePlugin")

val RequestTracePlugin = createRouteScopedPlugin(name = "RequestTracePlugin") {
    onCall { call ->
        val method = call.request.httpMethod
        val requestUri = call.request.uri
        logger.info(
            "Received request: $method $requestUri"
        )
    }
}

fun Application.configureCallLogging() {
    install(RequestTracePlugin)
    install(CallLogging) {
        level = INFO
        format { call ->
            val status = call.response.status()
            val path = call.request.path()
            val httpMethod = call.request.httpMethod.value
            "Request completed: ${status.getDescription()}  $httpMethod $path, status: $status"
        }
    }
}

private fun HttpStatusCode?.getDescription(): String {
    return when (this?.value) {
        in 200..299 -> "successful"
        else -> "with errors"
    }
}
