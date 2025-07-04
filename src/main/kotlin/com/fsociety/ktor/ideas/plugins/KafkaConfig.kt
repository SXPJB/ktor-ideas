package com.fsociety.ktor.ideas.plugins

import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.infraestructure.kafka.plugin.KtorKafkaPlugin
import io.ktor.server.application.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

fun Application.configureKafka() {
    install(KtorKafkaPlugin) {
        addProducer {
            //TODO: Auto generate koin dependency for producers ids
            id = "user-producer"
            valueSerializer = User.UserSerializer()
            topic = "user-topic"
        }

        addProducer {
            id = "string-serializer"
            topic = "string-serializer"
            valueSerializer = StringSerializer()
        }

        addConsumer {
            valueDeserializer = User.UserDeserializer()
            topics = listOf("user-topic")
            listener { user ->
                log.info("Recived $user")
            }
        }

        addConsumer {
            valueDeserializer = StringDeserializer()
            topics = listOf("string-serializer")
            listener { string ->
                log.info("Recived message: $string")
            }
        }
    }
}
