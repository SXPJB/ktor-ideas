package com.fsociety.ktor.ideas

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        client.get("/healthcheck").apply {
            assertEquals(HttpStatusCode.Companion.OK, status)
        }
    }

}