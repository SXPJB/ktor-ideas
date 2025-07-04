package com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.manager

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.model.KafkaConsumerWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Manages Kafka consumers within a Ktor application, allowing for dynamic addition, removal,
 * and lifecycle management of consumers. This class simplifies the process of handling multiple
 * Kafka consumers by encapsulating their creation, starting, and stopping in a centralized manager.
 *
 * Consumers are uniquely identified by an ID and registered with associated listener
 * functions to process consumed messages.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 */
class KtorKafkaConsumerManager {

    private val logger = logger()
    private val consumers = mutableMapOf<String, KafkaConsumerWrapper<*>>()

    /**
     * Creates and registers a new Kafka consumer with the specified identifier, consumer instance, and listener.
     *
     * If a consumer with the given identifier already exists, this method logs a warning and skips registration.
     * Otherwise, the consumer is wrapped and added to the internal registry for management.
     *
     * @param T The type of messages consumed by the Kafka consumer.
     * @param id A unique string identifier for the Kafka consumer being created.
     * @param consumer The instance of [KtorKafkaConsumer] used to handle message consumption.
     * @param listener A callback function to process each consumed message of type T.
     */
    fun <T> create(
        id: String,
        consumer: KtorKafkaConsumer<T>,
        listener: (T) -> Unit
    ) {
        if (consumers.containsKey(id)) {
            logger.warn("Consumer $id is already added. Skipping...")
            return
        }
        consumers[id] = KafkaConsumerWrapper(consumer, listener)
        logger.info("Consumer $id is added")
    }

    /**
     * Starts all Kafka consumers managed by this class in the provided coroutine scope.
     *
     * Each consumer is launched within its own coroutine, allowing them to process messages concurrently.
     * Logs are recorded for both the start of the operation and the initiation of each individual consumer.
     *
     * @param scope The [CoroutineScope] in which the consumers will be launched. This scope is used to
     *              manage the lifecycle of the coroutines created for each consumer.
     */
    fun startAll(scope: CoroutineScope) {
        logger.info("Starting kafka consumers")
        consumers.forEach { (id, consumer) ->
            scope.launch {
                logger.info("Starting kafka consumer with id $id")
                consumer.startListening()
            }
        }
    }

    /**
     * Stops all managed Kafka consumers.
     *
     * This function iterates through all registered Kafka consumers in the `KtorKafkaConsumerManager`
     * and stops their message consumption by invoking the `stopListening` method on each consumer.
     *
     * Logging is performed for each consumer to indicate the stopping process.
     *
     * Typically used during the shutdown phase or when a controlled termination of all Kafka consumers
     * is required to ensure cleanup of resources.
     */
    fun stopAll() {
        logger.info("Stopping kafka consumers")
        consumers.forEach { (id, consumer) ->
            logger.info("Stopping kafka consumer with id $id")
            consumer.stopListening()
        }
    }
}