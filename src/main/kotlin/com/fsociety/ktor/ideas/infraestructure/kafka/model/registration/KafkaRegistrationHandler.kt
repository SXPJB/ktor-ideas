package com.fsociety.ktor.ideas.infraestructure.kafka.model.registration

import com.fsociety.ktor.ideas.infraestructure.kafka.plugin.KtorKafkaPlugin

/**
 * Handles the registration of Kafka consumers and producers using the KtorKafkaPlugin.
 *
 * This class is responsible for processing Kafka registration requests and delegating the
 * registration handling to the appropriate internal methods, such as for Kafka consumers
 * and producers. It integrates closely with the KtorKafkaPlugin, allowing seamless
 * management of Kafka components.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param plugin The instance of KtorKafkaPlugin to manage Kafka consumers and producers.
 *
 */
class KafkaRegistrationHandler(
    private val plugin: KtorKafkaPlugin,
) {

    /**
     * Handles the registration of Kafka components such as consumers and producers.
     *
     * This method determines the type of the provided Kafka registration and processes
     * it accordingly. If the registration represents a Kafka consumer, it invokes the
     * consumer registration process. If it represents a Kafka producer, it invokes the
     * producer registration process.
     *
     * @param T The type of messages that the Kafka component will handle.
     * @param registration The Kafka registration entity representing either a consumer
     * or a producer to be processed. Must be an implementation of [KafkaRegistration].
     */
    fun <T> handle(registration: KafkaRegistration<T>) {
        when (registration) {
            is KtorKafkaConsumerRegistration -> registerConsumer(registration)
            is KtorKafkaProducerRegistration -> registerProducer(registration)
        }
    }

    /**
     * Registers a Kafka consumer using the provided registration details.
     *
     * This method adds a Kafka consumer to the plugin by associating it with a unique identifier,
     * the consumer instance, and a listener function for processing incoming messages of type T.
     *
     * @param T The type of messages handled by the Kafka consumer.
     * @param registration The [KtorKafkaConsumerRegistration] containing the unique identifier,
     * the Kafka consumer instance, and the listener function.
     */
    private fun <T> registerConsumer(registration: KtorKafkaConsumerRegistration<T>) {
        plugin.addConsumer(
            id = registration.id,
            consumer = registration.consumer,
            listener = registration.listener
        )
    }

    /**
     * Registers a Kafka producer within the Ktor Kafka plugin.
     *
     * This function adds the specified Kafka producer registration to the plugin's internal
     * producer manager, allowing it to send messages within the Kafka framework.
     *
     * @param T The type of messages produced by the Kafka producer.
     * @param registration The Kafka producer registration to be added. This includes the
     * unique identifier for the registration and the producer instance responsible for sending messages.
     */
    private fun <T> registerProducer(registration: KtorKafkaProducerRegistration<T>) {
        plugin.addProducer(
            id = registration.id,
            producer = registration.producer,
        )
    }
}