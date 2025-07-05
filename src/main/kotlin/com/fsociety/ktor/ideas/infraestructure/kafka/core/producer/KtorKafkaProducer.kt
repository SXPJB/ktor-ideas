package com.fsociety.ktor.ideas.infraestructure.kafka.core.producer

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

/**
 * A Kafka producer wrapper providing integration with Ktor and coroutine support.
 *
 * This class facilitates sending messages to a Kafka topic using Kafka's `Producer`.
 * It internally manages coroutines for efficient asynchronous processing of Kafka message delivery.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param kafkaProducer The Kafka producer instance responsible for sending messages.
 * @param messageTopic The name of the Kafka topic to which messages will be sent.
 * @param scope The coroutine scope within which Kafka producer operations will execute.
 */
class KtorKafkaProducer<T>(
    private val kafkaProducer: Producer<String, T>,
    private val messageTopic: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    /**
     * Logger instance for logging messages in the `KtorKafkaProducer` class.
     * Used for providing debug, error, and warning logs related to the Kafka producer's operations.
     */
    private val logger = logger()
    /**
     * Indicates whether the Kafka producer is closed. When `true`, the producer
     * is no longer operational, and further attempts to send messages will be ignored.
     * This variable is set to `true` during the cleanup process to prevent
     * further operations and ensure the producer is properly closed.
     */
    private var isClosed = false

    /**
     * Sends a message to Kafka by creating a new producer record with the provided message
     * and the configured topic, and then forwarding it to the Kafka producer.
     *
     * @param message The message to be sent to Kafka. It is a generic type representing the message payload.
     */
    fun send(message: T) {
        val record = ProducerRecord<String, T>(messageTopic, message)
        send(record)
    }

    /**
     * Sends a message with an associated key to a Kafka topic using a Kafka producer.
     *
     * @param message The message to be sent to the Kafka topic.
     * @param key The key associated with the message for partitioning.
     */
    fun send(message: T, key: String) {
        val record = ProducerRecord(messageTopic, key, message)
        send(record)
    }

    /**
     * Sends a Kafka producer record asynchronously. The method uses the provided coroutine scope
     * to handle the sending operation and logs the result or any errors that occur during the process.
     *
     * @param record the Kafka producer record to be sent, containing the topic, key, and value for the message
     */
    fun send(record: ProducerRecord<String, T>) {
        if (isClosed) {
            logger.warn("Producer is closed. Cannot send message.")
            return
        }
        scope.launch {
            runCatching {
                kafkaProducer.send(record) { metadata, exception ->
                    if (exception != null) {
                        logger.error("Error sending message: ${exception.message}", exception)
                    } else {
                        logger.debug("Message sent to topic: ${metadata.topic()}, partition: ${metadata.partition()}, offset: ${metadata.offset()}")
                    }
                }
            }.onFailure {
                logger.error("Exception in send coroutine", it)
            }
        }
    }

    /**
     * Closes the Kafka producer and its associated resources.
     *
     * This method marks the producer as closed, shuts down the underlying Kafka producer instance,
     * and cancels the CoroutineScope used for asynchronous operations. Once this method is called,
     * no further messages can be sent using the producer.
     */
    fun close() {
        isClosed = true
        kafkaProducer.close()
        scope.cancel()
    }
}