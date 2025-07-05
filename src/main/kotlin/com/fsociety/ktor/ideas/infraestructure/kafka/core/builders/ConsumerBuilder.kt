package com.fsociety.ktor.ideas.infraestructure.kafka.core.builders

import com.fsociety.ktor.ideas.infraestructure.kafka.common.Constants.BOOTSTRAP_SERVERS_PATH
import com.fsociety.ktor.ideas.infraestructure.kafka.common.Constants.KAFKA_GROUP_ID_PATH
import com.fsociety.ktor.ideas.infraestructure.kafka.common.createConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.common.getConfigProperty
import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.model.registration.KtorKafkaConsumerRegistration
import io.ktor.server.application.*
import org.apache.kafka.common.serialization.Deserializer
import java.util.*

/**
 * A builder class to configure and create a Kafka consumer registration using Ktor.
 *
 * This class provides a fluent API to define the necessary components for initializing
 * a Kafka consumer, including specifying the deserializer, bootstrap servers, group ID,
 * topics, and a listener function for processing consumed messages. The `build` method
 * validates the configuration and either uses an existing consumer or initializes a new one
 * using the provided parameters.
 *
 *  @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of the messages consumed by the Kafka consumer.
 * @param application The Ktor Application instance used to resolve configuration properties.
 *
 * TODO: Revisit how to add the topics to listen; maybe using kotlin DSL like:
 *          topics {
 *              topic("topic name")
 *          }
 */
class ConsumerBuilder<T>(
    private val application: Application
) {
    /**
     * A unique identifier for the Kafka consumer instance.
     *
     * This variable is used to distinguish between different consumer instances within the system.
     * By default, it is initialized with a randomly generated UUID to ensure uniqueness. It can
     * be explicitly set to a specific value if needed.
     *
     * The `id` is important in scenarios such as logging, monitoring, or registering the consumer.
     */
    var id: String = UUID.randomUUID().toString()

    /**
     * A nullable reference to a `KtorKafkaConsumer` instance, allowing flexible assignment and usage of a Kafka consumer.
     *
     * This property serves as a container for an optional `KtorKafkaConsumer` object and can be utilized within
     * the `ConsumerBuilder` class to configure, reference, or build a Kafka consumer dynamically.
     *
     * By default, the property is initialized to `null`, and it must be assigned a valid `KtorKafkaConsumer` instance
     * or configured during the build process using the features provided in the `ConsumerBuilder` class.
     *
     * @param T The type of messages consumed by this Kafka consumer.
     */
    var consumer: KtorKafkaConsumer<T>? = null

    /**
     * Deserializer used to convert Kafka message values into objects of type `T`.
     *
     * This property must be explicitly set if the `consumer` property is not provided. It is utilized
     * during the creation of a Kafka consumer to enable deserialization of message values retrieved
     * from Kafka topics.
     *
     * If neither `consumer` nor `valueDeserializer` is set, an exception will be thrown during the build
     * process when creating a Kafka consumer.
     *
     * Nullable to allow deferred initialization but must hold a non-null value before invoking the `build` method.
     *
     * @see ConsumerBuilder.build
     */
    var valueDeserializer: Deserializer<T>? = null

    /**
     * Represents the configuration property for Kafka bootstrap servers.
     *
     * This variable specifies the connection points for the Kafka cluster as a comma-separated
     * list of `host:port` pairs. It is used to establish the connection between Kafka clients
     * (producers or consumers) and the Kafka brokers.
     *
     * If not explicitly set, the value can optionally be retrieved from the application
     * configuration by referencing a predefined property path.
     *
     * This property is required for creating a Kafka client unless a pre-configured client
     * instance is provided.
     */
    var bootstrapServers: String? = null

    /**
     * Specifies the unique identifier for a Kafka consumer group.
     *
     * This property is used to identify the consumer group to which this Kafka consumer belongs.
     * Consumer groups allow multiple consumers to coordinate and share message consumption for
     * achieving scalability and fault tolerance. Within a group, each consumer is assigned a subset
     * of the partitions of a topic, ensuring no duplicate processing and efficient load balancing.
     *
     * If not explicitly set, the group identifier can be initialized from a configuration property
     * (e.g., "kafka.consumerGroupId") in the application's environment. It is a crucial parameter
     * for Kafka consumers to ensure proper consumption behavior and message offset management.
     *
     * This property is nullable to support delayed initialization but must have a non-null value
     * before constructing a valid consumer instance.
     */
    var groupId: String? = null

    /**
     * Represents the list of Kafka topics to which the consumer will subscribe.
     *
     * This property is mandatory and must contain at least one topic before building
     * the Kafka consumer. It identifies the target topics from which messages will
     * be consumed. Each topic specified here must exist on the Kafka broker that the
     * consumer connects to.
     *
     * An exception will be thrown during the build process if this property is
     * either not set or is an empty list, as consuming messages requires at least
     * one valid topic to subscribe to.
     */
    var topics: List<String> = emptyList()

    /**
     * A lambda function that acts as a callback to handle messages of type `T` consumed by the Kafka consumer.
     *
     * This property is nullable and must be explicitly set before building the Kafka consumer registration.
     * The listener is responsible for defining the processing logic for messages received from Kafka topics.
     *
     * If the listener is not set, an exception will be thrown during the build process, as it is a
     * required component for Kafka consumer functionality.
     */
    private var listener: ((T) -> Unit)? = null

    /**
     * Registers a listener function that processes Kafka consumer messages.
     *
     * This method sets the provided callback function as the listener which is invoked
     * for each message consumed from the Kafka topics. The listener must handle messages
     * of type `T`. Once registered, it acts as the core message processor for the consumer.
     *
     * @param block A function that takes a message of type `T` as input and is called for each consumed message.
     */
    fun listener(block: (T) -> Unit) {
        listener = block
    }

    /**
     * Builds and returns a configured instance of `KtorKafkaConsumerRegistration`.
     *
     * This method validates and ensures all required components needed to create the registration
     * are provided, either directly or derived from the application's configuration. The consumer
     * registration includes a listener for processing Kafka messages and either a pre-configured
     * Kafka consumer or a newly created one based on the provided settings.
     *
     * @throws IllegalArgumentException if any mandatory parameter (listener, consumer, or required
     * configuration) is missing or invalid.
     *
     * @return A fully constructed `KtorKafkaConsumerRegistration` ready for use in consuming
     * and processing Kafka messages.
     */
    internal fun build(): KtorKafkaConsumerRegistration<T> {
        val validListener = requireNotNull(listener) { "Handler must be provided" }

        consumer?.let { validConsumer ->
            return KtorKafkaConsumerRegistration(id, validConsumer, validListener)
        }

        val validValueDeserializer = requireNotNull(valueDeserializer) {
            "Either consumer or valueDeserializer must be provided"
        }

        val validBootstrapServers = requireNotNull(
            bootstrapServers ?: application.getConfigProperty(BOOTSTRAP_SERVERS_PATH)
        ) { "Either consumer, bootstrapServers, or kafka.bootstrapServers in environment must be provided" }

        val validGroupId = requireNotNull(
            groupId ?: application.getConfigProperty(KAFKA_GROUP_ID_PATH)
        ) {
            "Either consumer, groupId, or kafka.consumerGroupId in environment must be provided"
        }

        require(topics.isNotEmpty()) {
            "Either consumer, topics must be provided not empty"
        }

        return KtorKafkaConsumerRegistration(
            id = id,
            listener = validListener,
            consumer = KtorKafkaConsumer(
                kafkaConsumer = createConsumer(
                    bootstrapServers = validBootstrapServers,
                    deserializer = validValueDeserializer,
                    groupId = validGroupId
                ),
                topics = topics
            )
        )
    }
}