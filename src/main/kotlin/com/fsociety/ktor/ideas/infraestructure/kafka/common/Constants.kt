package com.fsociety.ktor.ideas.infraestructure.kafka.common

object Constants {
    const val PREFIX_PATH = "kafka"
    const val BOOTSTRAP_SERVERS_PATH = "$PREFIX_PATH.bootstrapServers"
    const val KAFKA_GROUP_ID_PATH = "$PREFIX_PATH.consumerGroupId"
}