package com.fsociety.ktor.ideas.http.controller

import arrow.core.Either
import com.fsociety.ktor.ideas.common.kafka.User
import com.fsociety.ktor.ideas.service.UserService
import org.apache.kafka.common.requests.ApiError

class UserController(
    private val userService: UserService,
) {

    fun sendUser(user: User): Either<ApiError, User> {
        return userService.sendToKafka(user)
    }
}