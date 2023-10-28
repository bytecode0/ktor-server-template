package com.example.routing.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UpdatePasswordRequest(
    val userId: String,
    val currentPassword: String,
    val newPassword: String
)