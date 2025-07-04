package com.fsociety.ktor.ideas.plugins.kafka.model

import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaConsumer

/**
 * A class to hold a consumer and its handler function.
 * This class is used internally by the KafkaConsumerPlugin to store the consumer and handler.
 *
 * @param consumer The Kafka consumer instance.
 * @param handler The function to handle consumed messages.
 */
data class ConsumerEntry<T : Any>(
    val consumer: KtorKafkaConsumer<T>,
    val handler: (T) -> Unit
) {
    /**
     * Start consuming messages from the Kafka topic.
     */
    fun startConsuming() {
        consumer.startConsuming(handler)
    }

    /**
     * Stop consuming messages from the Kafka topic.
     */
    fun stopConsuming() {
        consumer.stopConsuming()
    }
}