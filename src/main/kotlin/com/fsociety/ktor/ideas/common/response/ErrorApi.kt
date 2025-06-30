package com.fsociety.ktor.ideas.common.response

import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ErrorApi(
    val message: String,
    val status: String = HttpStatusCode.BadRequest.value.toString(),
    val timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
) {
    companion object {
        fun from(message: String, status: HttpStatusCode): ErrorApi {
            return ErrorApi(
                message = message,
                status = status.value.toString()
            )
        }
    }
}