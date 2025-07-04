# Ktor Kafka Plugin

A Ktor plugin for managing Kafka producers and consumers in a Ktor application.

## Features

- Register and manage multiple Kafka consumers
- Configure bootstrap servers and consumer group ID at the top level
- Kotlin DSL for easy configuration
- Automatic lifecycle management (starting/stopping consumers)
- Type-safe message handling
- Integration with Ktor's application lifecycle

## Usage

### Installation

Add the plugin to your application using the Kotlin DSL:

```kotlin
fun Application.configureKafka() {
    // Install the KtorKafkaPlugin with top-level configuration
    install(KtorKafkaPlugin) {
        // Set the bootstrap servers and group ID at the top level
        bootstrapServers = "localhost:9093"
        groupId = "my-consumer-group"

        // Register consumers using the DSL
        registerKafkaConsumer {
            // Configuration for this consumer
            // No need to specify bootstrapServers or groupId
            // as they are inherited from the top-level configuration
        }
    }

    // You can also register consumers outside the install block
    registerKafkaConsumerWithKtor {
        // Configuration for this consumer
        // Need to specify all configuration values
    }
}
```

For backward compatibility, you can also use the original style:

```kotlin
fun Application.configureKafka() {
    // Install the KafkaConsumerPlugin with DSL configuration
    install(KafkaConsumerPlugin) {
        // Register consumers here using the DSL
        registerKafkaConsumer {
            // Configuration for this consumer
            // Need to specify all configuration values
        }
    }
}
```

### Registering Consumers

There are two ways to register consumers with the plugin:

#### Method 1: Using a pre-configured KafkaConsumer instance

Register a pre-configured KafkaConsumer instance with the plugin:

```kotlin
// Get a pre-configured consumer from your dependency injection container
val kafkaMessageConsumer by inject<KtorKafkaConsumer<KafkaMessage>>()

// Register it with the plugin using the DSL
install(KtorKafkaPlugin) {
    // Set the bootstrap servers and group ID at the top level
    bootstrapServers = "localhost:9093"
    groupId = "my-consumer-group"

    registerKafkaConsumer {
        id = "kafka-message-consumer"  // Optional, auto-generated if not provided
        consumer = kafkaMessageConsumer
        listener { message ->
            log.info("Processed Kafka message: ${message.name}")
            // Handle the message as needed
        }
    }
}
```

#### Method 2: Creating a consumer on-the-fly with the deserializer

You can also create a consumer on-the-fly by providing the deserializer and other configuration options:

```kotlin
// Register a consumer with the plugin using the DSL
install(KtorKafkaPlugin) {
    // Set the bootstrap servers and group ID at the top level
    bootstrapServers = "localhost:9093"
    groupId = "my-consumer-group"

    registerKafkaConsumer {
        id = "on-the-fly-consumer"  // Optional, auto-generated if not provided
        valueDeserializer = KafkaMessage.KafkaMessageDeserializer()
        // No need to explicitly provide bootstrapServers or groupId
        // They will be taken from the top-level configuration
        // Only need to provide the topic
        topic = "message-topic"
        listener { message ->
            log.info("On-the-fly consumer received message: $message")
            // Handle the message as needed
        }
    }
}
```

#### Method 3: Registering a consumer outside the install block

You can also register a consumer outside the install block using the `registerKafkaConsumerWithKtor` function:

```kotlin
// Register a consumer outside the install block
registerKafkaConsumerWithKtor {
    id = "another-consumer"
    valueDeserializer = KafkaMessage.KafkaMessageDeserializer()
    // Need to provide all configuration values when registering outside the install block
    bootstrapServers = "localhost:9093"
    groupId = "my-consumer-group"
    topic = "another-topic"
    listener { message ->
        log.info("Another consumer received message: $message")
    }
}
```

#### Using the Backward-Compatible API

You can also use the original style for backward compatibility:

```kotlin
// Get a pre-configured consumer from your dependency injection container
val kafkaMessageConsumer by inject<KafkaConsumer<KafkaMessage>>()

// Register it with the plugin
install(KafkaConsumerPlugin) {
    registerKafkaConsumer {
        id = "kafka-message-consumer"  // Optional, auto-generated if not provided
        consumer = kafkaMessageConsumer
        listener { message ->
            log.info("Processed Kafka message: ${message.name}")
            // Handle the message as needed
        }
    }
}
```

### Complete Example

Here's a complete example showing all methods of registering consumers:

```kotlin
fun Application.configureKafka() {
    // Method 1: Using a pre-configured consumer from Koin
    val kafkaMessageConsumer by inject<KtorKafkaConsumer<KafkaMessage>>()

    // Install the KtorKafkaPlugin with top-level configuration
    install(KtorKafkaPlugin) {
        // Set the bootstrap servers and group ID at the top level
        bootstrapServers = "localhost:9093"
        groupId = "my-consumer-group"

        // Method 1: Register a consumer using a pre-configured KtorKafkaConsumer instance
        registerKafkaConsumer {
            id = "pre-configured-consumer"
            consumer = kafkaMessageConsumer
            listener { message ->
                log.info("Pre-configured consumer received message: $message")
                // Handle the message as needed
            }
        }

        // Method 2: Creating a consumer on-the-fly with the deserializer
        registerKafkaConsumer {
            id = "on-the-fly-consumer"
            valueDeserializer = KafkaMessage.KafkaMessageDeserializer()
            // No need to explicitly provide bootstrapServers or groupId
            // They will be taken from the top-level configuration
            // Only need to provide the topic
            topic = "message-topic"
            listener { message ->
                log.info("On-the-fly consumer received message: $message")
                // Handle the message as needed
            }
        }
    }

    // Method 3: Registering a consumer outside the install block
    registerKafkaConsumerWithKtor {
        id = "another-consumer"
        valueDeserializer = KafkaMessage.KafkaMessageDeserializer()
        // Need to provide all configuration values when registering outside the install block
        bootstrapServers = "localhost:9093"
        groupId = "my-consumer-group"
        topic = "another-topic"
        listener { message ->
            log.info("Another consumer received message: $message")
        }
    }

    // The plugin will automatically start all consumers when the application starts
    // and stop them when the application stops
}
```

You can also use the original style for backward compatibility:

```kotlin
fun Application.configureKafka() {
    // Get the pre-configured consumer from Koin
    val kafkaMessageConsumer by inject<KtorKafkaConsumer<KafkaMessage>>()

    // Install the KafkaConsumerPlugin with DSL configuration
    install(KafkaConsumerPlugin) {
        registerKafkaConsumer {
            id = "kafka-message-consumer"
            consumer = kafkaMessageConsumer
            listener { message ->
                log.info("Processed Kafka message: ${message.name}")
                // Handle the message as needed
            }
        }
    }
}
```

### Lifecycle Management

The plugin automatically manages the lifecycle of all registered consumers:

- Consumers are started when the application starts
- Consumers are stopped when the application stops

You can also manually start and stop all consumers:

```kotlin
// Start all consumers
application.kafkaConsumerPlugin.startAllConsumers()

// Stop all consumers
application.kafkaConsumerPlugin.stopAllConsumers()
```

### Advanced Usage

You can get a registered consumer by ID:

```kotlin
val consumer = application.kafkaConsumerPlugin.getConsumer<KafkaMessage>("kafka-message-consumer")
```

You can remove a consumer from the registry:

```kotlin
application.kafkaConsumerPlugin.removeConsumer("kafka-message-consumer")
```

## Implementation Details

The plugin uses a registry to keep track of all registered consumers and their handlers. Each consumer is identified by a unique ID. The plugin manages the lifecycle of all consumers, starting them when the application starts and stopping them when the application stops.

The plugin is implemented as a Ktor application plugin using the BaseApplicationPlugin interface. It integrates with Ktor's application lifecycle by subscribing to the ApplicationStarted and ApplicationStopping events.

The DSL is implemented using Kotlin's type-safe builders pattern, allowing for a clean and idiomatic way to configure the plugin and register consumers.

### Top-Level Configuration

The plugin now supports top-level configuration for bootstrap servers and consumer group ID. This allows you to set these values once at the plugin level, and they will be used by all consumers registered within the plugin's installation block. This makes the configuration more concise and reduces duplication.

### Package Structure

The plugin is organized into the following packages:

- `com.fsociety.ktor.ideas.plugins.kafka.core`: Contains the core plugin implementation
- `com.fsociety.ktor.ideas.plugins.kafka.config`: Contains configuration classes for the plugin
- `com.fsociety.ktor.ideas.plugins.kafka.model`: Contains model classes used by the plugin
- `com.fsociety.ktor.ideas.plugins.kafka`: Contains extension functions for the plugin

### Type Safety

The plugin uses Kotlin's type system to ensure type safety throughout the implementation. This eliminates the need for unsafe casts and null assertions, making the code more robust and less prone to runtime errors.

### Backward Compatibility

The plugin maintains backward compatibility with the original implementation through a facade pattern. This allows existing code to continue working without modification while benefiting from the improved implementation.

### Future Enhancements

In a future PR, the plugin will be enhanced to support Kafka producers as well, making it a complete solution for Kafka integration in Ktor applications.
