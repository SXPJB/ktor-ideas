package com.fsociety.ktor.ideas.infraestructure.kafka.model.registration

import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer

/**
 * Represents a registration entity for Kafka components, such as producers or consumers.
 *
 * This interface acts as a marker and provides a common structure for different types of Kafka registrations.
 * Implementations of this interface can define additional properties and behavior specific to their type,
 * such as consumers or producers.
 *
 * @param T The type of the data that the Kafka component will handle.
 */
sealed interface KafkaRegistration<T> {
    /**
     * Represents a unique identifier for a Kafka registration within the Kafka plugin system.
     *
     * This identifier is used to differentiate and reference specific Kafka registrations, such as
     * consumers or producers, managed by the plugin. Each registration must have a unique `id` to
     * ensure it can be individually tracked and utilized during lifecycle operations (e.g., adding,
     * starting, or stopping consumers and producers).
     *
     * Declared as part of the [KafkaRegistration] interface, this property ensures consistency
     * and serves as a key for correlating Kafka resources to their associated tasks in the system.
     */
    val id: String
}

/**
 * Represents a registration entity for a Kafka consumer using Ktor.
 *
 * This class defines the necessary components to register and manage a Kafka consumer in the system.
 * It includes the unique identifier for the registration, the consumer instance, and the listener
 * function to process incoming Kafka messages.
 *
 * @param T The type of messages to be consumed.
 * @property id A unique identifier for this registration.
 * @property consumer The Kafka consumer instance used to consume messages.
 * @property listener The function that processes incoming messages of type T.
 */
data class KtorKafkaConsumerRegistration<T>(
    override val id: String,
    val consumer: KtorKafkaConsumer<T>,
    val listener: (T) -> Unit
) : KafkaRegistration<T>

/**
 * Represents a registration for a Kafka producer in the Ktor Kafka plugin framework.
 *
 * @param T The type of messages produced by the Kafka producer.
 * @property id A unique identifier for the Kafka producer registration.
 * @property producer The associated Kafka producer responsible for sending messages.
 */
data class KtorKafkaProducerRegistration<T>(
    override val id: String,
    val producer: KtorKafkaProducer<T>,
) : KafkaRegistration<T>