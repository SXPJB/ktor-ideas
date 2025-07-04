package com.fsociety.ktor.ideas.infraestructure.kafka.model

import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer

/**
 * A wrapper class for managing the consumption of messages from Kafka topics.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of messages being consumed.
 * @param consumer The Kafka consumer instance used for consuming messages.
 * @param listener A callback function to process each message received.
 */
data class KafkaConsumerWrapper<T>(
    val consumer: KtorKafkaConsumer<T>,
    val listener: (T) -> Unit
) {

    fun startListening() {
        consumer.startListening(listener)
    }

    fun stopListening() {
        consumer.stopListening()
    }
}