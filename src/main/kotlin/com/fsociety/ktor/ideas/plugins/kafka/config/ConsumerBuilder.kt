package com.fsociety.ktor.ideas.plugins.kafka.config

import com.fsociety.ktor.ideas.common.kafka.config.JsonDeserializer
import com.fsociety.ktor.ideas.common.utils.createConsumer
import com.fsociety.ktor.ideas.plugins.kafka.core.KtorKafkaConsumer
import com.fsociety.ktor.ideas.plugins.kafka.model.ConsumerRegistration
import io.ktor.server.application.*
import org.apache.kafka.clients.consumer.Consumer
import java.util.*

/**
 * Builder class for configuring a Kafka consumer.
 * This class is used to build a ConsumerRegistration instance.
 */
class ConsumerBuilder<T : Any>(private val application: Application? = null) {
    /**
     * The unique identifier for this consumer. If not provided, a random UUID will be generated.
     */
    var id: String = UUID.randomUUID().toString()

    /**
     * The Kafka consumer instance.
     */
    var consumer: KtorKafkaConsumer<T>? = null

    /**
     * The deserializer for the value type.
     */
    var valueDeserializer: JsonDeserializer<T>? = null

    /**
     * The bootstrap servers.
     */
    var bootstrapServers: String? = null

    /**
     * The consumer group ID.
     */
    var groupId: String? = null

    /**
     * The topic to consume messages from.
     */
    var topic: String? = null

    /**
     * The handler function for consumed messages.
     */
    private var handler: ((T) -> Unit)? = null

    /**
     * Set the handler function for consumed messages.
     */
    fun listener(block: (T) -> Unit) {
        handler = block
    }

    /**
     * Retrieves a configuration property from the application environment.
     * @param path The path to the property in the configuration
     * @param defaultValue The default value to use if the property is not found or the application is not available
     * @return The value of the property or the default value
     */
    private fun getConfigProperty(path: String, defaultValue: String? = null): String? {
        return try {
            application?.environment?.config?.propertyOrNull(path)?.getString() ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * Build the consumer registration.
     * @throws IllegalArgumentException if handler is not provided and either consumer or all required properties are not provided
     */
    internal fun build(): ConsumerRegistration<T> {
        val validHandler = requireNotNull(handler) { "Handler must be provided" }

        // If consumer is provided, use it
        consumer?.let { validConsumer ->
            return ConsumerRegistration(id, validConsumer, validHandler)
        }

        // Otherwise, create a consumer from the provided properties
        val validDeserializer = requireNotNull(valueDeserializer) {
            "Either consumer or valueDeserializer must be provided"
        }

        // Get bootstrap servers from properties or environment
        val validBootstrapServers = requireNotNull(
            bootstrapServers ?: getConfigProperty("kafka.bootstrapServers", "localhost:9092")
        ) { "Either consumer, bootstrapServers, or kafka.bootstrapServers in environment must be provided" }


        // Get group ID from properties or environment
        val validGroupId = requireNotNull(
            groupId ?: getConfigProperty("kafka.consumerGroupId", "default-group")
        ) {
            "Either consumer, groupId, or kafka.consumerGroupId in environment must be provided"
        }

        // Get topic from properties or environment
        val validTopic = requireNotNull(topic) {
            "Either consumer, topic, or kafka.messageTopic in environment must be provided"
        }
        // Create the Kafka consumer
        val kafkaConsumer: Consumer<String, T> = createConsumer(
            bootstrapServers = validBootstrapServers,
            groupId = validGroupId,
            deserializer = validDeserializer
        )

        // Create the KtorKafkaConsumer
        val ktorKafkaConsumer = KtorKafkaConsumer(
            kafkaConsumer = kafkaConsumer,
            messageTopic = validTopic
        )

        return ConsumerRegistration(id, ktorKafkaConsumer, validHandler)
    }
}
