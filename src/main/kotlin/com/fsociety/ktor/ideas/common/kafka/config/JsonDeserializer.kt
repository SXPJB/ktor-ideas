package com.fsociety.ktor.ideas.common.kafka.config

import com.fsociety.ktor.ideas.common.utils.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.apache.kafka.common.serialization.Deserializer

@OptIn(ExperimentalSerializationApi::class)
open class JsonDeserializer<T>(
    private val serializer: KSerializer<T>
) : Deserializer<T> {

    private val logger = logger()

    private val json = Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.Builtins.SnakeCase
        explicitNulls = false
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? {
        return runCatching {
            data?.let {
                readValue(it)
            }
        }.onFailure {
            logger.error(it.message)
        }.getOrNull()
    }

    private fun readValue(data: ByteArray): T {
        val string = data.toString(Charsets.UTF_8)
        return json.decodeFromString(serializer, string)
    }
}