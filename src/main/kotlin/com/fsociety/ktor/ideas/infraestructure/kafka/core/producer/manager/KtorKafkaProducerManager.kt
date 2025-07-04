package com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.manager

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer

/**
 * Manages Kafka producers within a Ktor application.
 *
 * Provides utility functions to create, retrieve, and close Kafka producers
 * through a centralized manager. This class is intended to streamline
 * the management of multiple Kafka producers in an application, ensuring
 * proper initialization and cleanup of resources.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 */
class KtorKafkaProducerManager {
    private val logger = logger()

    private val producers = mutableMapOf<String, KtorKafkaProducer<*>>()

    /**
     * Registers a new Kafka producer with the specified identifier if it is not already present in the manager.
     *
     * @param id The unique identifier for the Kafka producer. This identifier is used to reference the producer within the manager.
     * @param producer The Kafka producer instance to be registered. The producer will handle sending messages of the specified type.
     * @param T The type of messages the Kafka producer will handle.
     */
    fun <T> create(
        id: String,
        producer: KtorKafkaProducer<T>
    ) {
        if (producers.containsKey(id)) {
            logger.warn("Producer $id is already added. Skipping...")
            return
        }
        producers[id] = producer
        logger.info("Producer $id is added")
    }

    /**
     * Closes all Kafka producers managed by this instance.
     *
     * This method iterates through all registered Kafka producers, logs their identifiers,
     * and invokes the `close` method on each producer to release their resources.
     *
     * It is typically used during the shutdown phase of the application to ensure proper cleanup
     * and prevent resource leaks.
     */
    fun closeAll() {
        logger.info("Closing kafka producers")
        producers.forEach { (id, producer) ->
            logger.info("Closing kafka producer with id $id")
            producer.close()
        }
    }

    /**
     * Retrieves a Kafka producer associated with the specified identifier.
     *
     * @param id The unique identifier of the Kafka producer to retrieve.
     * @return The Kafka producer corresponding to the provided identifier.
     * @throws IllegalArgumentException if no producer with the specified identifier is found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getById(id: String): KtorKafkaProducer<T> {
        //TODO: Find way to retriever the KtorKafkaProducer without UNCHECKED_CAST
        return producers[id] as? KtorKafkaProducer<T>
            ?: throw IllegalArgumentException("Producer with id '$id' not found")
    }
}