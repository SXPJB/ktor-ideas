package com.fsociety.ktor.ideas.service

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.streaming.KafkaProducer
import org.apache.kafka.common.requests.ApiError

class UserService(
    private val kafkaProducer: KafkaProducer
) {
    fun sendToKafka(user: User): Either<ApiError, User> {
        return try {
            kafkaProducer.sendUser(user)
            user.right()
        } catch (ex: Throwable) {
            ApiError.fromThrowable(ex).left()
        }
    }
}