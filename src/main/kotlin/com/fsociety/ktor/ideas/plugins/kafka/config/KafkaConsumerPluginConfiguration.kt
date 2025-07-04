package com.fsociety.ktor.ideas.plugins.kafka.config

import com.fsociety.ktor.ideas.plugins.kafka.model.ConsumerRegistration
import io.ktor.server.application.*

/**
 * Configuration class for the KtorKafkaPlugin.
 * This class is used to configure the KtorKafkaPlugin during installation.
 */
class KtorKafkaPluginConfiguration(private val application: Application) {
    private val registrations = mutableListOf<ConsumerRegistration<*>>()

    /**
     * The bootstrap servers for Kafka.
     */
    var bootstrapServers: String? = null

    /**
     * The consumer group ID.
     */
    var groupId: String? = null

    /**
     * Register a Kafka consumer with the plugin.
     * @param block The configuration block for the consumer.
     */
    fun <T : Any> registerKafkaConsumer(block: ConsumerBuilder<T>.() -> Unit) {
        val builder = ConsumerBuilder<T>(application).apply {
            // Pass the top-level configuration to the builder
            this.bootstrapServers = this@KtorKafkaPluginConfiguration.bootstrapServers
            this.groupId = this@KtorKafkaPluginConfiguration.groupId
            block()
        }
        registrations.add(builder.build())
    }

    /**
     * Get all consumer registrations.
     * @return A list of all registered consumers.
     */
    internal fun getRegistrations(): List<ConsumerRegistration<*>> = registrations
}
