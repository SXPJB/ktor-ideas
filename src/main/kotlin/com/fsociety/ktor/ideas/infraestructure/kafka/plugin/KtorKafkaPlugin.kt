package com.fsociety.ktor.ideas.infraestructure.kafka.plugin

import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.KtorKafkaConsumer
import com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer.manager.KtorKafkaConsumerManager
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.manager.KtorKafkaProducerManager
import com.fsociety.ktor.ideas.infraestructure.kafka.model.registration.KafkaRegistrationHandler
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import com.fsociety.ktor.ideas.infraestructure.kafka.config.KtorKafkaPluginConfiguration.Builder as KtorKafkaPluginBuilder

/**
 * KtorKafkaPlugin is responsible for managing Kafka consumers and producers within a Ktor application.
 * The plugin facilitates the lifecycle of Kafka components, such as starting and stopping consumers
 * and producers when the application events occur.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 * TODO: Creation topics Manager, add KeySerialization, remove unsafe cast
 */
class KtorKafkaPlugin : CoroutineScope {
    /**
     * Represents a [Job] instance used to manage the lifecycle of coroutines in the `KtorKafkaPlugin`.
     * Acts as the parent job for all coroutines within the plugin, enabling structured concurrency
     * and streamlined cancellation when the plugin is stopped or disposed.
     */
    private val job = Job()
    /**
     * Represents the coroutine context used by the plugin, combining the `Dispatchers.IO` dispatcher
     * and the associated `Job` for managing the lifecycle of coroutines started within this context.
     *
     * This property ensures that operations requiring coroutine context, such as launching or
     * managing asynchronous tasks, are executed on the IO dispatcher and are scoped to the `Job` to
     * properly handle cancellation and resource cleanup when the plugin's lifecycle ends.
     */
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    /**
     * Manages Kafka consumers for the `KtorKafkaPlugin`.
     *
     * This property provides centralized management for Kafka consumers, including creation, starting,
     * and stopping. It is used internally by the plugin to handle consumer lifecycle operations such as
     * adding new consumers, starting all consumers within a given coroutine scope, and stopping all consumers.
     *
     * Responsibilities include:
     * - Adding and managing Kafka consumers using unique identifiers.
     * - Starting all registered Kafka consumers.
     * - Stopping all registered Kafka consumers.
     *
     * The `consumerManager` is an instance of `KtorKafkaConsumerManager` and interacts closely with
     * `KtorKafkaConsumer`, allowing listeners to process incoming messages for specific Kafka topics.
     */
    private val consumerManager = KtorKafkaConsumerManager()
    /**
     * Manages the lifecycle and operations of Kafka producers within the Ktor Kafka plugin.
     *
     * This property provides access to an instance of [KtorKafkaProducerManager], which is responsible for creating,
     * retrieving, and closing Kafka producers. It acts as a centralized producer manager, ensuring consistent
     * management of Kafka producer instances for the application.
     *
     * The [producerManager] allows you to add new producers, retrieve existing ones by their identifier,
     * and close all producers when necessary. It simplifies the integration of Kafka producers into the
     * Ktor application by encapsulating related logic and providing a unified management interface.
     *
     * Usage within the Ktor Kafka plugin includes:
     * - Adding new Kafka producers via the `create` method.
     * - Retrieving an existing producer by its identifier via the `getById` method.
     * - Closing all managed producers via the `closeAll` method, ensuring proper resource cleanup.
     *
     * It is typically utilized in conjunction with other components of the Ktor Kafka plugin, such as
     * `KtorKafkaConsumerManager`, to facilitate seamless Kafka producer-consumer workflows.
     */
    private val producerManager = KtorKafkaProducerManager()

    /**
     * Starts all registered Kafka consumers within the plugin.
     *
     * This method invokes the `startAll` function of `KtorKafkaConsumerManager`
     * and provides the current instance as a coroutine scope for launching consumer processing jobs.
     */
    fun start() {
        consumerManager.startAll(this)
    }

    /**
     * Stops all active Kafka consumers and closes all Kafka producers managed by the plugin.
     *
     * This method invokes the `stopAll` function on the `consumerManager` to stop all running Kafka consumers.
     * Additionally, it calls the `closeAll` function on the `producerManager` to close all active Kafka producers.
     *
     * Typically used during the shutdown phase of the application to ensure proper resource cleanup.
     */
    fun stop() {
        consumerManager.stopAll()
        producerManager.closeAll()
    }

    /**
     * Adds a Kafka consumer to be managed by the plugin.
     *
     * @param id The unique identifier for the Kafka consumer.
     * @param consumer An instance of [KtorKafkaConsumer] to handle Kafka message consumption.
     * @param listener A lambda function that processes each consumed Kafka message.
     */
    fun <T> addConsumer(
        id: String,
        consumer: KtorKafkaConsumer<T>,
        listener: (T) -> Unit
    ) = consumerManager.create(id, consumer, listener)

    /**
     * Retrieves a Kafka producer associated with the specified identifier.
     *
     * @param id The unique identifier of the Kafka producer to retrieve.
     * @return The Kafka producer corresponding to the provided identifier.
     */
    fun <T> getProducer(id: String) = producerManager.getById<T>(id)


    /**
     * Adds a Kafka producer to the plugin's internal producer manager.
     *
     * @param id The unique identifier for the producer.
     * @param producer The Kafka producer instance to be registered.
     * @param T The type of messages the producer will handle.
     */
    fun <T> addProducer(
        id: String,
        producer: KtorKafkaProducer<T>,
    ) = producerManager.create(id, producer)

    /**
     * Companion object that acts as a plugin for integrating Kafka functionality with a Ktor application.
     * This plugin allows configuration and setup of Kafka consumers and producers, enabling the application
     * to send and receive messages via Kafka topics.
     *
     * This plugin lifecycle is tied to the application lifecycle:
     * - When the application starts, the plugin's `start` method is invoked to initialize the Kafka integration.
     * - When the application stops, the plugin's `stop` method is invoked to gracefully stop consumers and producers.
     *
     * The plugin can be configured using the `KtorKafkaPluginBuilder`, where consumers and producers
     * can be registered along with other Kafka-specific settings.
     */
    companion object Plugin :
        BaseApplicationPlugin<Application, KtorKafkaPluginBuilder, KtorKafkaPlugin> {
        /**
         * Represents the unique key for the `KtorKafkaPlugin` which is used for identifying and managing
         * the plugin instance within the Ktor application environment.
         */
        override val key = AttributeKey<KtorKafkaPlugin>("KtorKafkaPlugin")

        /**
         * Installs the KtorKafkaPlugin into the specified application pipeline and configures it using the provided builder.
         * Handles the setup and registration of Kafka consumers and producers, as well as lifecycle events.
         *
         * @param pipeline The application pipeline where the plugin will be installed.
         * @param configure A lambda to configure the `KtorKafkaPluginBuilder` with necessary settings for Kafka consumers and producers.
         * @return The configured instance of `KtorKafkaPlugin` that has been installed in the application pipeline.
         */
        override fun install(
            pipeline: Application,
            configure: KtorKafkaPluginBuilder.() -> Unit
        ): KtorKafkaPlugin {
            val configuration = KtorKafkaPluginBuilder(pipeline)
                .apply(configure)
                .build()

            val plugin = KtorKafkaPlugin()
            val registry = KafkaRegistrationHandler(plugin)

            configuration.getKafkaRegistration().forEach { registration ->
                registry.handle(registration)
            }

            pipeline.monitor.subscribe(ApplicationStarted) {
                plugin.start()
            }

            pipeline.monitor.subscribe(ApplicationStopping) {
                plugin.stop()
            }

            return plugin
        }
    }
}