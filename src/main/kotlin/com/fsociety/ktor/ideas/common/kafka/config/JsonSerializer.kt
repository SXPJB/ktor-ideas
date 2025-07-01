package com.fsociety.ktor.ideas.common.kafka.config

import com.fsociety.ktor.ideas.common.utils.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.apache.kafka.common.serialization.Serializer

@OptIn(ExperimentalSerializationApi::class)
open class JsonSerializer<T>(
    private val serializer: KSerializer<T>
) : Serializer<T> {

    private val logger = logger()

    private val json = Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
        explicitNulls = false
    }

    override fun serialize(topic: String, data: T): ByteArray {
        return runCatching {
            readValue(data)
        }.getOrElse {
            logger.error(it.message)
            throw IllegalStateException("Can't serialize $data")
        }
    }

    private fun readValue(data: T): ByteArray {
        return json.encodeToString(serializer, data)
            .toByteArray(Charsets.UTF_8)
    }
}