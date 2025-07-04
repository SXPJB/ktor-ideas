package com.fsociety.ktor.ideas.infraestructure.kafka.serialization.json

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.apache.kafka.common.serialization.Serializer

/**
 * A serializer implementation for converting objects to JSON for Kafka messages.
 * This class serializes objects of a specified type into byte arrays, which can be sent as Kafka messages.
 * It uses the kotlinx.serialization framework for serialization.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of object this serializer handles.
 * @param serializer The kotlinx.serialization serializer used for transforming objects of type T into JSON.
 * @param json The JSON configuration that can be customized. Defaults to a configuration that uses snake_case naming strategy.
 */
@OptIn(ExperimentalSerializationApi::class)
open class JsonSerializer<T>(
    private val serializer: KSerializer<T>,
    private val json: Json = Json {
        namingStrategy = JsonNamingStrategy.SnakeCase
    }
) : Serializer<T> {

    private val logger = logger()

    /**
     * Serializes the given data of type T into a JSON byte array representation.
     * If the serialization process fails, an error is logged, and an IllegalStateException is thrown.
     *
     * @param topic the Kafka topic associated with the message. This parameter is not used in the serialization process.
     * @param data the object of type T to be serialized.
     * @return a byte array representing the serialized JSON of the input data.
     * @throws IllegalStateException if the serialization process encounters an error.
     */
    override fun serialize(topic: String, data: T): ByteArray {
        return runCatching { readValue(data) }.getOrElse {
            logger.error(it.message)
            throw IllegalStateException("Can't serialize $data")
        }
    }

    /**
     * Serializes the provided data of type T into a JSON byte array using the configured serializer.
     *
     * @param data The object of type T to be serialized.
     * @return A byte array containing the serialized JSON representation of the provided data.
     */
    private fun readValue(data: T): ByteArray {
        return json.encodeToString(serializer, data)
            .toByteArray(Charsets.UTF_8)
    }
}