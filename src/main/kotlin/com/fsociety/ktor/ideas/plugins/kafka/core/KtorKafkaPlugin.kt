package com.fsociety.ktor.ideas.plugins.kafka.core

import com.fsociety.ktor.ideas.common.utils.logger
import com.fsociety.ktor.ideas.plugins.kafka.config.KtorKafkaPluginConfiguration
import com.fsociety.ktor.ideas.plugins.kafka.model.ConsumerEntry
import com.fsociety.ktor.ideas.plugins.kafka.model.ConsumerRegistration
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A Ktor plugin for managing Kafka producers and consumers.
 * This plugin allows registering multiple consumers and manages their lifecycle.
 */
class KtorKafkaPlugin : CoroutineScope {
    private val logger = logger()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    // Registry to keep track of all consumers and their handlers
    private val consumers = mutableMapOf<String, TypedConsumerEntry>()

    /**
     * Interface for type-safe consumer entries.
     * This allows us to store consumers of different types in the same map.
     */
    private interface TypedConsumerEntry {
        fun startConsuming()
        fun stopConsuming()
    }

    /**
     * Implementation of TypedConsumerEntry that wraps a ConsumerEntry.
     */
    private class TypedConsumerEntryImpl<T : Any>(
        val entry: ConsumerEntry<T>
    ) : TypedConsumerEntry {
        override fun startConsuming() {
            entry.startConsuming()
        }

        override fun stopConsuming() {
            entry.stopConsuming()
        }
    }

    /**
     * Register a consumer with a handler function.
     * @param id A unique identifier for this consumer
     * @param consumer The KafkaConsumer instance
     * @param handler The function to handle consumed messages
     */
    fun <T : Any> registerConsumer(
        id: String,
        consumer: KtorKafkaConsumer<T>,
        handler: (T) -> Unit
    ) {
        if (consumers.containsKey(id)) {
            logger.warn("Consumer with ID $id is already registered. It will be replaced.")
        }
        val entry = ConsumerEntry(consumer, handler)
        consumers[id] = TypedConsumerEntryImpl(entry)
        logger.info("Registered Kafka consumer with ID: $id")
    }

    /**
     * Start all registered consumers.
     */
    fun startAllConsumers() {
        logger.info("Starting all Kafka consumers...")
        consumers.forEach { (id, entry) ->
            launch {
                logger.info("Starting Kafka consumer with ID: $id")
                entry.startConsuming()
            }
        }
    }

    /**
     * Stop all registered consumers.
     */
    fun stopAllConsumers() {
        logger.info("Stopping all Kafka consumers...")
        consumers.forEach { (id, entry) ->
            logger.info("Stopping Kafka consumer with ID: $id")
            entry.stopConsuming()
        }
        job.cancel()
    }

    /**
     * Get a registered consumer by ID.
     * @param id The consumer ID
     * @return The KafkaConsumer instance or null if not found
     */
    fun <T : Any> getConsumer(id: String): KtorKafkaConsumer<T>? {
        // This is a safe cast because we're using the type parameter T
        // to ensure type safety at the call site
        val entry = consumers[id] as? TypedConsumerEntryImpl<T> ?: return null
        return entry.entry.consumer
    }

    /**
     * Remove a consumer from the registry.
     * @param id The consumer ID
     * @return true if the consumer was removed, false otherwise
     */
    fun removeConsumer(id: String): Boolean {
        val entry = consumers.remove(id)
        if (entry != null) {
            entry.stopConsuming()
            logger.info("Removed Kafka consumer with ID: $id")
            return true
        }
        return false
    }

    companion object Plugin :
        BaseApplicationPlugin<Application, KtorKafkaPluginConfiguration, KtorKafkaPlugin> {
        override val key = AttributeKey<KtorKafkaPlugin>("KtorKafkaPlugin")

        override fun install(
            pipeline: Application,
            configure: KtorKafkaPluginConfiguration.() -> Unit
        ): KtorKafkaPlugin {
            val configuration = KtorKafkaPluginConfiguration(pipeline).apply(configure)
            val plugin = KtorKafkaPlugin()

            // Register all consumers from the configuration
            configuration.getRegistrations().forEach { registration ->
                registerConsumer(plugin, registration)
            }

            // Start all consumers when the application starts
            pipeline.monitor.subscribe(ApplicationStarted) {
                plugin.startAllConsumers()
            }

            // Stop all consumers when the application stops
            pipeline.monitor.subscribe(ApplicationStopping) {
                plugin.stopAllConsumers()
            }

            return plugin
        }

        /**
         * Register a consumer with the plugin.
         * This method uses type erasure to register consumers of different types.
         */
        private fun registerConsumer(plugin: KtorKafkaPlugin, registration: ConsumerRegistration<*>) {
            // Use reflection to call the registerConsumer method with the correct type
            val method = plugin::class.java.getDeclaredMethod(
                "registerConsumer",
                String::class.java,
                KtorKafkaConsumer::class.java,
                Function1::class.java
            )
            method.invoke(plugin, registration.id, registration.consumer, registration.handler)
        }
    }
}