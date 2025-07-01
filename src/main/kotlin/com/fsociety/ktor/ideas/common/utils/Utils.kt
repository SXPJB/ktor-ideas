package com.fsociety.ktor.ideas.common.utils

import com.fsociety.ktor.ideas.common.kafka.config.JsonDeserializer
import com.fsociety.ktor.ideas.common.kafka.config.JsonSerializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

inline fun <reified T : Any> ConsumerRecord<*, *>.handleAs(
    logger: Logger,
    crossinline handler: (T) -> Unit
) {
    val value = this.value()
    if (value is T) {
        handler(value)
    } else {
        logger.error("Received message is not of type ${T::class.simpleName}")
    }
}

/**
 * Creates a Kafka producer for messages.
 * @param bootstrapServers The Kafka bootstrap servers.
 * @param serializer The serializer for the value type.
 * @return A Producer for messages.
 */
fun <T> createProducer(
    bootstrapServers: String,
    serializer: JsonSerializer<T>,
): Producer<String, T> {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer::class.java)
    }
    return KafkaProducer(props)
}

/**
 * Creates a Kafka consumer for messages.
 * @param bootstrapServers The Kafka bootstrap servers.
 * @param groupId The consumer group ID.
 * @param deserializer The deserializer for the value type.
 * @return A Consumer for messages.
 */
fun <T> createConsumer(
    bootstrapServers: String,
    groupId: String,
    deserializer: JsonDeserializer<T>,
): org.apache.kafka.clients.consumer.Consumer<String, T> {
    val props = Properties().apply {
        put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, groupId)
        put(
            org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            org.apache.kafka.common.serialization.StringDeserializer::class.java
        )
        put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer::class.java)
        put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    }
    return org.apache.kafka.clients.consumer.KafkaConsumer(props)
}