package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.common.kafka.KafkaMessage
import com.fsociety.ktor.ideas.streaming.KafkaConsumer
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

/**
 * Configures Kafka consumers and starts them.
 */
fun Application.configureKafka() {
    val kafkaConsumer by inject<KafkaConsumer<KafkaMessage>>()

    // Start consuming messages in a background coroutine
    launch {
        kafkaConsumer.startConsuming { message ->
            log.info("Processed Kafka message: ${message.name}")
            // Handle the message as needed
        }
    }

    // Add a shutdown hook to stop the consumer when the application stops
    monitor.subscribe(ApplicationStopping) {
        kafkaConsumer.stopConsuming()
    }
}
