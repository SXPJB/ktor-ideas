package com.fsociety.ktor.ideas.streaming

import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.common.utils.logger
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducer(
    private val userProProducer: Producer<String, User>,
    private val userTopic: String,
) {

    private val logger = logger()

    fun sendUser(user: User): User {
        val record = ProducerRecord<String, User>(userTopic, user)
        userProProducer.send(record) { metadata, exception ->
            if (exception != null) {
                logger.error(exception.message, exception)
            } else {
                logger.info("User ${user.username} was sent to kafka")
            }
        }
        return user
    }
}