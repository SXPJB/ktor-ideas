package com.fsociety.ktor.ideas.common.kafka

import com.fsociety.ktor.ideas.common.kafka.config.JsonDeserializer
import com.fsociety.ktor.ideas.common.kafka.config.JsonSerializer
import kotlinx.serialization.Serializable

/**
 * Data class representing a User for Kafka messages.
 * This class is used for serializing and deserializing user data to/from Kafka.
 */
@Serializable
data class User(
    val username: String,
    val password: String,
) {
    class UserSerializer : JsonSerializer<User>(serializer())
    class UserDeserializer : JsonDeserializer<User>(serializer())
}
