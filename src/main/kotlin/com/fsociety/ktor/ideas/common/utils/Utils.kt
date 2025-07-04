package com.fsociety.ktor.ideas.common.utils

import com.fsociety.ktor.ideas.common.kafka.config.JsonDeserializer
import com.fsociety.ktor.ideas.common.kafka.config.JsonSerializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

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