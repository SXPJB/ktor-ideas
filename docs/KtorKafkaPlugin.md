# Ktor Kafka Plugin

A Ktor plugin for managing Kafka producers and consumers in a Ktor application.

## Features

- Register and manage multiple Kafka consumers and producers
- Configure bootstrap servers and consumer group ID at the top level
- Kotlin DSL for easy configuration
- Automatic lifecycle management (starting/stopping consumers and producers)
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
        addConsumer {
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

    addConsumer {
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

    addConsumer {
        id = "on-the-fly-consumer"  // Optional, auto-generated if not provided
        valueDeserializer = JsonDeserializer<User>(serializer()) // Using kotlinx.serialization
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
    valueDeserializer = JsonDeserializer<User>(serializer())
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

### Registering Producers

There are three ways to register producers with the plugin:

1. Using a pre-configured producer instance
2. Creating a producer on-the-fly with a serializer
3. Registering a producer outside the install block

#### Method 1: Using a pre-configured producer instance

You can register a pre-configured producer instance with the plugin:

```kotlin
// Get a pre-configured producer from your dependency injection container
val kafkaMessageProducer by inject<KtorKafkaProducer<KafkaMessage>>()

// Register it with the plugin
install(KtorKafkaPlugin) {
    addProducer {
        id = "kafka-message-producer"  // Optional, auto-generated if not provided
        producer = kafkaMessageProducer
    }
}
```

#### Method 2: Creating a producer on-the-fly with a serializer

You can also create a producer on-the-fly by providing a serializer:

```kotlin
// Register a producer with the plugin using the DSL
install(KtorKafkaPlugin) {
    // Set the bootstrap servers at the top level
    bootstrapServers = "localhost:9093"

    addProducer {
        id = "on-the-fly-producer"  // Optional, auto-generated if not provided
        valueSerializer = KafkaMessage.KafkaMessageSerializer()
        // No need to explicitly provide bootstrapServers
        // It will be taken from the top-level configuration
        // Only need to provide the topic
        topic = "message-topic"
    }
}
```

#### Method 3: Registering a producer outside the install block

You can also register a producer outside the install block using the `registerKafkaProducerWithKtor` function:

```kotlin
// Register a producer outside the install block
registerKafkaProducerWithKtor {
    id = "another-producer"
    valueSerializer = KafkaMessage.KafkaMessageSerializer()
    // Need to provide all configuration values when registering outside the install block
    bootstrapServers = "localhost:9093"
    topic = "another-topic"
}
```

### Using Producers

Once you have registered a producer, you can retrieve it and use it to send messages:

```kotlin
// Get a producer by ID
val producer = getKafkaProducer<KafkaMessage>("kafka-message-producer")

// Send a message without a key
producer.send(KafkaMessage("Hello, Kafka!"))

// Send a message with a key
producer.send(KafkaMessage("Hello, Kafka!"), "message-key")

// Send a custom producer record
val record = ProducerRecord<String, KafkaMessage>("message-topic", "message-key", KafkaMessage("Hello, Kafka!"))
producer.send(record)
```

### Complete Example

Here's a complete example showing all methods of registering consumers and producers:

```kotlin
fun Application.configureKafka() {
    // Method 1: Using pre-configured components from Koin
    val kafkaMessageConsumer by inject<KtorKafkaConsumer<KafkaMessage>>()
    val kafkaMessageProducer by inject<KtorKafkaProducer<KafkaMessage>>()

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

        // Method 1: Register a producer using a pre-configured KtorKafkaProducer instance
        addProducer {
            id = "pre-configured-producer"
            producer = kafkaMessageProducer
        }

        // Method 2: Creating a producer on-the-fly with the serializer
        addProducer {
            id = "on-the-fly-producer"
            valueSerializer = KafkaMessage.KafkaMessageSerializer()
            // No need to explicitly provide bootstrapServers
            // It will be taken from the top-level configuration
            topic = "message-topic"
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

    // Method 3: Registering a producer outside the install block
    registerKafkaProducerWithKtor {
        id = "another-producer"
        valueSerializer = KafkaMessage.KafkaMessageSerializer()
        // Need to provide all configuration values when registering outside the install block
        bootstrapServers = "localhost:9093"
        topic = "another-topic"
    }

    // The plugin will automatically start all consumers when the application starts
    // and stop them when the application stops

    // Using a producer to send messages
    val producer = getKafkaProducer<KafkaMessage>("another-producer")
    producer.send(KafkaMessage("Hello, Kafka!"))
}
```

### Lifecycle Management

The plugin automatically manages the lifecycle of all registered consumers and producers:

- Consumers are started when the application starts
- Consumers and producers are stopped when the application stops

You can also manually start and stop all consumers:

```kotlin
// Start all consumers
application.ktorKafkaPlugin.start()

// Stop all consumers and close all producers
application.ktorKafkaPlugin.stop()
```

### Advanced Usage

You can get a registered producer by ID:

```kotlin
val producer = application.getKafkaProducer<KafkaMessage>("kafka-message-producer")
```

You can also get a producer directly from the plugin:

```kotlin
val producer = application.ktorKafkaPlugin.getProducer<KafkaMessage>("kafka-message-producer")
```

## Implementation Details

The plugin uses registries to keep track of all registered consumers and producers. Each component is identified by a unique ID. The plugin manages the lifecycle of all components, starting consumers when the application starts and stopping consumers and closing producers when the application stops.

The plugin is implemented as a Ktor application plugin using the BaseApplicationPlugin interface. It integrates with Ktor's application lifecycle by subscribing to the ApplicationStarted and ApplicationStopping events.

The DSL is implemented using Kotlin's type-safe builders pattern, allowing for a clean and idiomatic way to configure the plugin and register consumers and producers.

### Top-Level Configuration

The plugin supports top-level configuration for bootstrap servers and consumer group ID. This allows you to set these values once at the plugin level, and they will be used by all components registered within the plugin's installation block. Bootstrap servers are used by both consumers and producers, while the group ID is only used by consumers. This makes the configuration more concise and reduces duplication.

### Package Structure

The plugin is organized into the following packages:

- `com.fsociety.ktor.ideas.infraestructure.kafka.plugin`: Contains the core plugin implementation
- `com.fsociety.ktor.ideas.infraestructure.kafka.config`: Contains configuration classes for the plugin
- `com.fsociety.ktor.ideas.infraestructure.kafka.model`: Contains model classes used by the plugin
- `com.fsociety.ktor.ideas.infraestructure.kafka.common`: Contains extension functions and utilities for the plugin
- `com.fsociety.ktor.ideas.infraestructure.kafka.core.consumer`: Contains consumer implementation classes
- `com.fsociety.ktor.ideas.infraestructure.kafka.core.producer`: Contains producer implementation classes
- `com.fsociety.ktor.ideas.infraestructure.kafka.serialization`: Contains serialization and deserialization classes

### Type Safety

The plugin uses Kotlin's type system to ensure type safety throughout the implementation. This eliminates the need for unsafe casts and null assertions, making the code more robust and less prone to runtime errors.

### Backward Compatibility

The plugin maintains backward compatibility with the original implementation through a facade pattern. This allows existing code to continue working without modification while benefiting from the improved implementation.
