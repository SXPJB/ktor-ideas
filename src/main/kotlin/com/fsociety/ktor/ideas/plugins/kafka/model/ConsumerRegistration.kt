package com.fsociety.ktor.ideas.plugins.kafka.model

import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaConsumer

/**
 * Data class to hold a consumer registration.
 * This class is used to store the information needed to register a Kafka consumer.
 *
 * @param id The unique identifier for this consumer.
 * @param consumer The Kafka consumer instance.
 * @param handler The function to handle consumed messages.
 */
data class ConsumerRegistration<T : Any>(
    val id: String,
    val consumer: KtorKafkaConsumer<T>,
    val handler: (T) -> Unit
)