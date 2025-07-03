package com.fsociety.ktor.ideas.plugins.kafka.core

import com.fsociety.ktor.ideas.common.utils.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import java.time.Duration
import kotlin.coroutines.CoroutineContext

/**
 * A generic Kafka consumer that can consume messages of any type.
 * @param T The type of messages to consume.
 * @param kafkaConsumer The Kafka consumer instance.
 * @param messageTopic The topic to consume messages from.
 */
class KtorKafkaConsumer<T : Any>(
    private val kafkaConsumer: Consumer<String, T>,
    private val messageTopic: String,
) : CoroutineScope {
    private val logger = logger()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var isRunning = false

    /**
     * Start consuming messages from the Kafka topic.
     * @param handler The function to handle the consumed messages.
     */
    fun startConsuming(handler: (T) -> Unit) {
        if (isRunning) {
            logger.warn("Consumer is already running")
            return
        }

        isRunning = true
        kafkaConsumer.subscribe(listOf(messageTopic))

        launch {
            try {
                while (isRunning) {
                    val records: ConsumerRecords<String, T> =
                        kafkaConsumer.poll(Duration.ofMillis(100))
                    records.forEach { record ->
                        val value = record.value()
                        if (value != null) {
                            logger.info("Received message: $value")
                            handler(value)
                        } else {
                            logger.error("Received null message")
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while consuming messages", e)
            } finally {
                kafkaConsumer.close()
                isRunning = false
            }
        }
    }

    /**
     * Stop consuming messages from the Kafka topic.
     */
    fun stopConsuming() {
        isRunning = false
        job.cancel()
    }

    // No companion object needed as we're using the constructor directly
}