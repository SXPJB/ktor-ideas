package com.fsociety.ktor.ideas.common.kafka

import com.fsociety.ktor.ideas.common.kafka.config.JsonDeserializer
import kotlinx.serialization.Serializable

/**
 * Data class representing the Kafka message format.
 * This class is used for serializing and deserializing messages to/from Kafka.
 */
@Serializable
data class KafkaMessage(
    val count: String,
    val name: String,
    val status: String
) {
    class KafkaMessageDeserializer : JsonDeserializer<KafkaMessage>(serializer())
}
