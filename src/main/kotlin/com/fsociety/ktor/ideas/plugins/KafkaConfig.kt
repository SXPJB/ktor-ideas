package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.common.kafka.KafkaMessage
import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaPlugin
import io.ktor.server.application.*

/**
 * Configures Kafka consumers and starts them.
 */
fun Application.configureKafka() {
    install(KtorKafkaPlugin) {
        registerKafkaConsumer {
            id = "on-the-fly-consumer"
            valueDeserializer = KafkaMessage.KafkaMessageDeserializer()
            topic = "message-topic"
            listener { message ->
                log.info("On-the-fly consumer received message: $message")
            }
        }
    }
}
