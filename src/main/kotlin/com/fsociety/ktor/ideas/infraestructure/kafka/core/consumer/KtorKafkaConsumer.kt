package com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.Consumer
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Defines the polling duration used by the Kafka consumer when fetching records from subscribed topics.
 * Determines how long the consumer will wait if no records are immediately available.
 */
private val POLL_DURATION = Duration.ofMillis(100L)

/**
 * A Kafka consumer wrapper for integration with Ktor and Kafka, utilizing Kotlin's coroutines.
 *
 * This class facilitates consuming messages from specified Kafka topics and delegates message
 * processing to a provided listener function. It leverages coroutines for consumption, allowing
 * for non-blocking and concurrent operations. The consumer is configured to automatically poll
 * Kafka for new messages from the subscribed topics.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of messages consumed by the Kafka consumer.
 * @param kafkaConsumer The actual Kafka consumer instance responsible for message polling.
 * @param topics A list of topics to which the consumer subscribes for message consumption.
 * @param scope The coroutine scope used for launching the consumer job. Defaults to IO dispatcher.
 */
class KtorKafkaConsumer<T>(
    private val kafkaConsumer: Consumer<String, T>,
    private val topics: List<String>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val logger = logger()
    private val isRunning = AtomicBoolean(false)
    private var consumeJob: Job? = null

    /**
     * Starts consuming messages from the Kafka topics specified for the consumer.
     * Once started, it continuously polls for new messages and passes each non-null
     * message to the provided listener function until stopped or an error occurs.
     *
     * @param listener A callback function that processes each received message of type T.
     *                 This function is invoked for each non-null message consumed from the topics.
     */
    fun startListening(listener: (T) -> Unit) {
        if (!isRunning.compareAndSet(false, true)) {
            logger.debug("Consumer is already running")
            return
        }
        kafkaConsumer.subscribe(topics)
        consumeJob = scope.launch {
            try {
                while (isRunning.get()) {
                    val records = kafkaConsumer.poll(POLL_DURATION)
                    for (record in records) {
                        record.value()?.let(listener) ?: logger.warn("Received null message")
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while consuming messages", e)
            } finally {
                kafkaConsumer.close()
                isRunning.set(false)
            }
        }
    }

    /**
     * Stops the Kafka consumer from listening to incoming messages.
     *
     * This method cancels the active job that is consuming messages and marks the consumer
     * as no longer running. A log entry is recorded to confirm that the consumer has stopped.
     *
     * Note: Ensure this method is called when you want to safely and explicitly stop the consumer.
     */
    fun stopListening() {
        isRunning.set(false)
        consumeJob?.cancel()
        logger.info("Consumer is stopped")
    }
}