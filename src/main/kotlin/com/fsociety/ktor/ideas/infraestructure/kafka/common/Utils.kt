package com.fsociety.ktor.ideas.infraestructure.kafka.common

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Creates a logger instance for the specified type `T`.
 *
 * This function provides a generic and inline way to initialize a logger for any class.
 * The logger is created using the `LoggerFactory` and associated with the specified class type.
 *
 * @return A logger instance for the class `T`.
 */
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

/**
 * Creates a Kafka producer instance configured with the specified bootstrap servers and value serializer.
 *
 * @param bootstrapServers The Kafka bootstrap servers, used to connect the producer to the Kafka cluster.
 * @param serializer The serializer for the value type of the Kafka messages produced.
 * @return A Kafka producer instance of type [Producer] with a String key and a generic value.
 */
fun <T> createProducer(
    bootstrapServers: String,
    serializer: Serializer<T>,
): Producer<String, T> {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer::class.java)
    }
    return KafkaProducer(props)
}

/**
 * Creates a Kafka consumer configured with the specified properties.
 *
 * This method initializes a Kafka consumer with the provided bootstrap servers,
 * group ID, and deserializer for the value type. The key deserializer is fixed
 * to use a String deserializer, and the consumer is set to start consuming messages
 * from the earliest available offset.
 *
 * @param T The type of the value for the Kafka consumer.
 * @param bootstrapServers A comma-separated list of host and port pairs to use for establishing the initial connection to the Kafka cluster.
 * @param groupId The unique group identifier for the Kafka consumer. Consumers in the same group share a group ID.
 * @param deserializer The deserializer used for the value in the Kafka consumer.
 * @return A configured instance of [KafkaConsumer] for consuming messages from a Kafka topic.
 */
fun <T> createConsumer(
    bootstrapServers: String,
    groupId: String,
    deserializer: Deserializer<T>,
): Consumer<String, T> {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java
        )
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer::class.java)
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }
    return KafkaConsumer(props)
}