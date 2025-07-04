package com.fsociety.ktor.ideas.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.infraestructure.kafka.core.producer.KtorKafkaProducer
import org.apache.kafka.common.requests.ApiError

class UserService(
    private val kafkaProducer: KtorKafkaProducer<User>,
    private val stringKafkaProducer: KtorKafkaProducer<String>,
) {
    fun sendToKafka(user: User): Either<ApiError, User> {
        return try {
            kafkaProducer.send(user)
            stringKafkaProducer.send(user.username)
            user.right()
        } catch (ex: Throwable) {
            ApiError.fromThrowable(ex).left()
        }
    }
}
