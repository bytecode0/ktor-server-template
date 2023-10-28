package com.example.domain.events

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
}
