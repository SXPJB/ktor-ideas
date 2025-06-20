package com.fsociety.ktor.ideas.domain.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PersonEntity : IntIdTable("person") {
    val name = varchar("name", 10)
    val lastName = varchar("last_name", 50)
    val email = varchar("email", 100)
    val createBy = varchar("create_by", 20)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedBy = varchar("updated_by", 20).nullable()
    val updatedAt = datetime("updated_at")
        .defaultExpression(CurrentDateTime)
        .nullable()
}