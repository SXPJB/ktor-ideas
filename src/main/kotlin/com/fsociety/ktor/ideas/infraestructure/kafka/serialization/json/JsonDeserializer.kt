package com.fsociety.ktor.ideas.infraestructure.kafka.serialization.json

import com.fsociety.ktor.ideas.infraestructure.kafka.common.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.apache.kafka.common.serialization.Deserializer


/**
 * A deserializer implementation for processing JSON data in Kafka messages.
 * This class deserializes messages from a Kafka topic into objects of a specified type
 * using a provided kotlinx.serialization serializer.
 *
 * @author Emmanuel H. Ramirez (@sxpjb)
 *
 * @param T The type of object this deserializer converts the JSON data into.
 * @param serializer The kotlinx.serialization serializer used for deserialization.
 * @param json The JSON configuration that can be customized. By default, it uses a naming strategy
 * for converting keys to snake_case.
 */
@OptIn(ExperimentalSerializationApi::class)
open class JsonDeserializer<T>(
    private val serializer: KSerializer<T>,
    private val json: Json = Json {
        namingStrategy = JsonNamingStrategy.Builtins.SnakeCase
    },
) : Deserializer<T> {

    private val logger = logger()

    /**
     * Deserializes the given byte array into an object of type T using a JSON serializer.
     *
     * @param topic the topic associated with the message, or null. This parameter is unused in the method.
     * @param data the byte array to be deserialized. If null, the method returns null.
     * @return the deserialized object of type T if the operation is successful, or null if an error occurs or data is null.
     */
    override fun deserialize(topic: String?, data: ByteArray?): T? {
        return runCatching {
            data?.let { readValue(it) }
        }.onFailure {
            logger.error(it.message)
        }.getOrNull()
    }

    /**
     * Decodes the provided byte array into an object of type T using the specified serializer.
     *
     * @param data The byte array to be deserialized.
     * @return The deserialized object of type T.
     */
    private fun readValue(data: ByteArray): T {
        val string = data.toString(Charsets.UTF_8)
        return json.decodeFromString(serializer, string)
    }
}
