package com.fsociety.ktor.ideas

import com.fsociety.ktor.ideas.common.request.CreatePersonRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EndpointsIntegrationTest {

    @Test
    fun testUserEndpoint() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        val response = client.get("/user/")
        assertEquals(HttpStatusCode.OK, response.status)
        val responseText = response.bodyAsText()
        assertTrue(responseText.contains("User Management API"))
    }

    @Test
    fun testCreatePersonEndpoint() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        val personRequest = CreatePersonRequest(
            name = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            createdBy = "test"
        )

        // Create a custom JSON configuration that matches the server's configuration
        val jsonConfig = Json {
            prettyPrint = true
            namingStrategy = kotlinx.serialization.json.JsonNamingStrategy.SnakeCase
            explicitNulls = false
        }

        val requestJson = jsonConfig.encodeToString(personRequest)
        println("[DEBUG_LOG] Person request with snake_case: $requestJson")

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
        }

        println("[DEBUG_LOG] Create person response status: ${response.status}")
        val responseBody = response.bodyAsText()
        println("[DEBUG_LOG] Create person response body: $responseBody")

        assertEquals(HttpStatusCode.Created, response.status)

        // The response also uses snake_case naming strategy
        val normalizedResponse = responseBody.replace("\\s".toRegex(), "")
        assertTrue(normalizedResponse.contains("\"name\":\"John\""))
        assertTrue(normalizedResponse.contains("\"last_name\":\"Doe\""))
        assertTrue(normalizedResponse.contains("\"email\":\"john.doe@example.com\""))
    }

    @Test
    fun testGetAllPersonsEndpoint() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        val response = client.get("/user/all")
        assertEquals(HttpStatusCode.OK, response.status)
        // Since we created a person in the previous test, we should have at least one person
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("["))  // Should be an array
    }

    @Test
    fun testHealthCheckEndpoint() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        val response = client.get("/healthcheck")
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        println("[DEBUG_LOG] Health check response: $responseBody")

        // Remove whitespace and check if the response contains the expected value
        val normalizedResponse = responseBody.replace("\\s".toRegex(), "")
        assertTrue(normalizedResponse.contains("\"status\":\"OK\""))
    }
}
