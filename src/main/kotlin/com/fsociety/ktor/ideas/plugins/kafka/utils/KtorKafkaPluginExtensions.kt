package com.fsociety.ktor.ideas.plugins.kafka.utils

import com.fsociety.ktor.ideas.plugins.kafka.config.ConsumerBuilder
import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaPlugin
import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaConsumer
import io.ktor.server.application.*

/**
 * Extension function to access the KtorKafkaPlugin.
 */
val Application.ktorKafkaPlugin: KtorKafkaPlugin get() = plugin(KtorKafkaPlugin)

/**
 * Extension function to register a consumer with the KtorKafkaPlugin.
 */
fun <T : Any> Application.registerKafkaConsumerWithKtor(
    id: String,
    consumer: KtorKafkaConsumer<T>,
    handler: (T) -> Unit
) {
    ktorKafkaPlugin.registerConsumer(id, consumer, handler)
}

/**
 * Extension function to register a consumer with the KtorKafkaPlugin using the DSL.
 */
fun <T : Any> Application.registerKafkaConsumerWithKtor(block: ConsumerBuilder<T>.() -> Unit) {
    val builder = ConsumerBuilder<T>(this).apply(block)
    val registration = builder.build()
    ktorKafkaPlugin.registerConsumer(registration.id, registration.consumer, registration.handler)
}
