package com.fsociety.ktor.ideas.infraestructure.kafka.config

import com.fsociety.ktor.ideas.infraestructure.kafka.core.builders.ConsumerBuilder
import com.fsociety.ktor.ideas.infraestructure.kafka.core.builders.ProducerBuilder
import com.fsociety.ktor.ideas.infraestructure.kafka.model.registration.KafkaRegistration
import io.ktor.server.application.*

/**
 * Configuration class for the Ktor Kafka plugin.
 *
 * This class encapsulates the configuration needed for working with producers and consumers in a Kafka setup.
 * It provides a way to manage and access Kafka registrations, which represent the various Kafka components defined
 * in the application.
 *
 *  @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @constructor Private constructor to enforce usage of the Builder for constructing instances.
 * @param kafkaRegistrations A list of Kafka registrations, either consumers or producers, that are part of the configuration.
 */
class KtorKafkaPluginConfiguration private constructor(
    private val kafkaRegistrations: List<KafkaRegistration<*>>,
) {

    /**
     * Retrieves the list of Kafka registrations managed by the current plugin configuration.
     *
     * This method provides access to all Kafka registration entities, including consumers and producers,
     * as defined within the configuration. These registrations encapsulate the metadata and settings
     * required to interact with Kafka, such as unique identifiers and component-specific configurations.
     *
     * @return A list of [KafkaRegistration] instances associated with the current plugin configuration.
     */
    fun getKafkaRegistration() = kafkaRegistrations

    /**
     * Provides a builder for configuring Kafka consumers and producers in a Ktor Kafka Plugin setup.
     *
     * This class allows for defining Kafka consumers and producers with the option to set
     * shared properties like `bootstrapServers` and `groupId`. The created consumers
     * and producers are stored as registrations, which are finalized into a [KtorKafkaPluginConfiguration].
     *
     * @constructor Initializes the builder with the given [application].
     * @property bootstrapServers Optional property to configure Kafka bootstrap servers for the builder.
     * @property groupId Optional property to configure the Kafka consumer group ID for the builder.
     */
    class Builder(private val application: Application) {
        /**
         * A mutable list holding the registrations of Kafka components, such as producers and consumers.
         *
         * This list is used to store and manage instances of [KafkaRegistration], which represent
         * the configuration and setup for Kafka producers or consumers. It's populated by `addConsumer`
         * and `addProducer` functions, and its contents are finalized when building the
         * `KtorKafkaPluginConfiguration`.
         *
         * The purpose of this property is to maintain the lifecycle and centralized tracking
         * of all Kafka registrations associated with the plugin system.
         */
        private val registrations = mutableListOf<KafkaRegistration<*>>()

        /**
         * Specifies the Kafka cluster's bootstrap servers used for establishing the initial connection.
         *
         * This property is utilized to configure the address(es) of the Kafka brokers that act as
         * the contact point(s) for the clients. It supports a comma-separated list of host and port pairs.
         *
         * Example usages include setting this value in the context of adding Kafka consumers or producers
         * to ensure proper communication with the specified Kafka cluster.
         *
         * @see Builder.addConsumer
         * @see Builder.addProducer
         */
        var bootstrapServers: String? = null

        /**
         * Specifies the consumer group ID used by Kafka to manage message consumption assignments.
         *
         * The `groupId` property determines which consumer group a Kafka consumer belongs to,
         * enabling Kafka to coordinate and distribute messages across multiple consumers within the same group.
         * This property is essential for implementing consumer groups in Kafka, ensuring that messages
         * from a topic are distributed among all members of the group rather than being consumed by all consumers independently.
         *
         * A `null` or unspecified value for `groupId` indicates that no specific consumer group is being used,
         * and the consumer may function as an independent entity without group-based coordination.
         *
         * This property is primarily utilized when building or configuring Kafka consumers.
         */
        var groupId: String? = null

        /**
         * Adds a Kafka consumer registration using the specified configuration block.
         *
         * This method creates a `ConsumerBuilder` instance and applies the provided configuration
         * block to customize the consumer settings, such as bootstrap servers, group ID, topics,
         * and message listener. The configured consumer is then registered for message consumption.
         *
         * @param T The type of the messages consumed by the Kafka consumer.
         * @param block A lambda with the receiver of `ConsumerBuilder<T>` to configure the Kafka consumer.
         */
        fun <T : Any> addConsumer(block: ConsumerBuilder<T>.() -> Unit) {
            val builder = ConsumerBuilder<T>(application).apply {
                this.bootstrapServers = this@Builder.bootstrapServers
                this.groupId = this@Builder.groupId
                block()
            }
            registrations.add(builder.build())
        }

        /**
         * Adds a producer configuration to the current Kafka setup. This method allows the custom
         * configuration of a Kafka producer instance, including details such as the serializer, topic,
         * and other parameters. The configuration is defined via a lambda block, which is executed
         * within the context of a `ProducerBuilder`.
         *
         * @param T The type of messages that the producer will handle.
         * @param block A lambda function to configure the `ProducerBuilder` with custom Kafka producer settings.
         */
        fun <T : Any> addProducer(block: ProducerBuilder<T>.() -> Unit) {
            val builder = ProducerBuilder<T>(application).apply {
                this.bootstrapServers = this@Builder.bootstrapServers
                block()
            }
            registrations.add(builder.build())
        }

        /**
         * Builds and returns a `KtorKafkaPluginConfiguration` instance based on the current state of the builder.
         *
         * @return A configured instance of `KtorKafkaPluginConfiguration` containing all Kafka registrations.
         */
        fun build(): KtorKafkaPluginConfiguration {
            return KtorKafkaPluginConfiguration(registrations.toList())
        }
    }
}