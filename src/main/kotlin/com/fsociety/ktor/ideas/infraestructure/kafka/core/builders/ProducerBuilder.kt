package com.fsociety.ktor.ideas.infraestructure.kafka.core.builders

import com.fsociety.ktor.ideas.infraestructure.kafka.common.createProducer
import com.fsociety.ktor.ideas.infraestructure.kafka.common.Constants.BOOTSTRAP_SERVERS_PATH
import com.fsociety.ktor.ideas.infraestructure.kafka.common.getConfigProperty
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer
import com.fsociety.ktor.ideas.infraestructure.kafka.model.registration.KtorKafkaProducerRegistration
import io.ktor.server.application.*
import org.apache.kafka.common.serialization.Serializer
import java.util.*

/**
 * A builder class to facilitate the creation and configuration of a `KtorKafkaProducer` or
 * its registration via `KtorKafkaProducerRegistration` with flexible setup options.
 *
 *  @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of the message payload for the Kafka producer.
 * @param application The Ktor application instance used to access environment configurations
 * such as properties for Kafka configuration.
 */
class ProducerBuilder<T>(
    private val application: Application
) {
    /**
     * A unique identifier for the producer. It can be explicitly set or defaults to
     * a randomly generated UUID. This identifier is used to distinguish between different
     * producer instances.
     */
    var id: String = UUID.randomUUID().toString()

    /**
     * Kafka producer instance that can be used to send messages to a Kafka topic.
     *
     * This property holds an instance of [KtorKafkaProducer], which provides coroutine-based
     * support for producing messages to Kafka. It is optional and may be null, typically
     * initialized directly or during the configuration process within the containing builder.
     *
     * If this variable is not explicitly set, a new producer is created during the build process
     * using the specified serializers, bootstrap servers, and topic information.
     *
     * @see KtorKafkaProducer
     */
    var producer: KtorKafkaProducer<T>? = null

    /**
     * Serializer used to convert objects of type `T` into a format suitable for Kafka message production.
     *
     * This property must be set explicitly if the `producer` property is not provided. It is utilized
     * during the construction of a Kafka producer to enable message serialization before sending to Kafka.
     *
     * If neither `producer` nor `valueSerializer` is provided, an exception will be thrown during the
     * build process.
     *
     * @see ProducerBuilder.build
     */
    var valueSerializer: Serializer<T>? = null

    /**
     * Represents the configuration property for Kafka bootstrap servers.
     *
     * This variable specifies the connection endpoints for the Kafka cluster
     * in the format of a comma-separated list of `host:port` pairs. It is used
     * to establish the connection between the Kafka producers or consumers
     * and the Kafka brokers.
     *
     * If not explicitly set, the value can be retrieved from the application
     * configuration using the predefined property path "kafka.bootstrapServers".
     *
     * This property is required for creating a Kafka producer or consumer unless
     * a pre-configured producer or consumer instance is provided.
     */
    var bootstrapServers: String? = null

    /**
     * Specifies the Kafka topic to which the producer will send messages.
     *
     * This property identifies the target topic in the Kafka broker where the serialized messages
     * will be published. It is a critical configuration parameter and must be explicitly set,
     * unless a custom producer instance is provided to the builder.
     *
     * If the topic is not set, an exception will be thrown during the build process, as it is
     * required to construct a valid Kafka producer instance.
     *
     * Nullable to allow delayed initialization, but must be assigned a non-null value prior to
     * calling the build method.
     */
    var topic: String? = null


    /**
     * Builds and returns a `KtorKafkaProducerRegistration` instance for the configured Kafka producer.
     *
     * This method performs the following operations:
     * - If a `producer` is already set, it wraps it in a `KtorKafkaProducerRegistration` and returns it.
     * - If a `producer` is not set, it validates the required properties (`valueSerializer`, `bootstrapServers`, and `topic`),
     *   creates a Kafka producer using the `createProducer` function, and wraps it in a `KtorKafkaProducerRegistration`.
     *
     * @return A `KtorKafkaProducerRegistration` instance containing a producer and the associated configuration.
     * @throws IllegalStateException if required configuration parameters are not provided.
     */
    internal fun build(): KtorKafkaProducerRegistration<T> {

        producer?.let { validProducer ->
            return KtorKafkaProducerRegistration(id, validProducer)
        }

        val validSerializer = requireNotNull(valueSerializer) {
            "Either producer or valueSerializer must be provided"
        }

        val validBootstrapServers = requireNotNull(
            bootstrapServers ?: application.getConfigProperty(BOOTSTRAP_SERVERS_PATH)
        ) { "Either producer, bootstrapServers, or kafka.bootstrapServers in environment must be provided" }

        val validTopic = requireNotNull(topic) {
            "Either producer or topic must be provided"
        }

        return KtorKafkaProducerRegistration(
            id = id,
            producer = KtorKafkaProducer(
                kafkaProducer = createProducer(
                    bootstrapServers = validBootstrapServers,
                    serializer = validSerializer
                ),
                messageTopic = validTopic
            )
        )
    }
}