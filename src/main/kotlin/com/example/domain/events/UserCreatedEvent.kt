package com.example.domain.events

import com.example.domain.entities.Entity
import com.example.domain.entities.UserEntity
import java.util.UUID

sealed class Event {
    data class UserCreatedEvent(
        val userId: UUID,
        val username: String,
        val email: String
    ): Event()

    data class UserPasswordUpdatedEvent(
        val userId: UUID,
        val username: String,
        val email: String
    ): Event()

    data class ProjectCreatedEvent(
        val createdBy: UserEntity,
        val project: Entity.ProjectEntity
    ): Event()
}
