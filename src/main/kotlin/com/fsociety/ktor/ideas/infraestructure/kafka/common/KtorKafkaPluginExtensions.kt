package com.fsociety.ktor.ideas.infraestructure.kafka.common

import com.fsociety.ktor.ideas.infraestructure.kafka.core.builders.ConsumerBuilder
import com.fsociety.ktor.ideas.infraestructure.kafka.core.builders.ProducerBuilder
import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer
import com.fsociety.ktor.ideas.infraestructure.kafka.plugin.KtorKafkaPlugin
import io.ktor.server.application.*

/**
 * Provides access to the `KtorKafkaPlugin` within the application.
 *
 * This property retrieves an instance of the `KtorKafkaPlugin` installed in the Ktor application.
 * The plugin manages Kafka consumers and producers, enabling seamless integration of Kafka
 * functionality into the application.
 *
 * Usage of this property allows for access to the plugin's API to register consumers and producers,
 * and to manage their lifecycles in synchronization with the application lifecycle.
 */
val Application.ktorKafkaPlugin: KtorKafkaPlugin get() = plugin(KtorKafkaPlugin)

/**
 * Registers a Kafka consumer with the Ktor application.
 *
 * This method integrates a Kafka consumer with the Ktor environment, allowing the application to
 * process messages from Kafka topics using the provided handler function. The consumer is managed
 * by the internal `KtorKafkaPlugin`.
 *
 * @param T The type of messages consumed by the Kafka consumer.
 * @param id A unique identifier for the Kafka consumer.
 * @param consumer An instance of [KtorKafkaConsumer] responsible for subscribing to Kafka topics and polling messages.
 * @param handler A lambda function that processes each message consumed from Kafka.
 */
fun <T : Any> Application.registerKafkaConsumerWithKtor(
    id: String,
    consumer: KtorKafkaConsumer<T>,
    handler: (T) -> Unit
) {
    ktorKafkaPlugin.addConsumer(id, consumer, handler)
}

/**
 * Registers a Kafka consumer within a Ktor application using a customizable configuration block.
 *
 * This function allows configuring and registering a Kafka consumer by utilizing the `ConsumerBuilder`
 * to set up topics, bootstrap servers, consumer group ID, deserializers, and a message-handling listener.
 * The consumer is then integrated with the provided Ktor application.
 *
 * @param T The type of messages consumed by the Kafka consumer.
 * @param block A lambda block where the `ConsumerBuilder` can be configured to initialize the Kafka consumer.
 */
fun <T : Any> Application.registerKafkaConsumerWithKtor(block: ConsumerBuilder<T>.() -> Unit) {
    val builder = ConsumerBuilder<T>(this).apply(block)
    val registration = builder.build()
    ktorKafkaPlugin.addConsumer(registration.id, registration.consumer, registration.listener)
}

/**
 * Registers a Kafka producer with the Ktor application through the KtorKafka plugin.
 *
 * @param id A unique identifier for the Kafka producer to be registered.
 * @param producer An instance of [KtorKafkaProducer] that facilitates Kafka message production.
 * @param T The type of messages the producer will handle.
 */
fun <T : Any> Application.registerKafkaProducerWithKtor(
    id: String,
    producer: KtorKafkaProducer<T>
) {
    ktorKafkaPlugin.addProducer(id, producer)
}

/**
 * Registers a Kafka producer with the Ktor application using a customizable builder block.
 * This method allows for the creation and configuration of a Kafka producer instance or its registration.
 * The producer is added to the Ktor Kafka plugin for managing Kafka producer instances within the application.
 *
 * @param T The type of the message payload the Kafka producer will handle.
 * @param block A lambda function applied to a [ProducerBuilder] to configure the Kafka producer.
 *              The builder provides options to specify properties such as `valueSerializer`,
 *              `bootstrapServers`, and `topic`, or to directly set a `producer` instance.
 */
fun <T : Any> Application.registerKafkaProducerWithKtor(block: ProducerBuilder<T>.() -> Unit) {
    val builder = ProducerBuilder<T>(this).apply(block)
    val registration = builder.build()
    ktorKafkaPlugin.addProducer(registration.id, registration.producer)
}

/**
 * Retrieves a Kafka producer with the specified identifier for sending messages to Kafka topics.
 *
 * @param id The unique identifier associated with the specific Kafka producer instance to retrieve.
 * @return An instance of [KtorKafkaProducer] configured for the specified producer identifier.
 */
fun <T : Any> Application.getKafkaProducer(id: String): KtorKafkaProducer<T> {
    return ktorKafkaPlugin.getProducer(id)
}

/**
 * Retrieves a configuration property value from the Ktor application's environment config.
 *
 * This method looks up a property from the application's environment configuration
 * using the specified `path`. If no property exists at the given `path`, it returns `null`.
 *
 * @param path The key or path of the configuration property to retrieve.
 * @return The value of the configuration property as a string, or `null` if the property is not found.
 */
fun Application.getConfigProperty(path: String): String? {
    return environment.config.propertyOrNull(path)?.getString()
}