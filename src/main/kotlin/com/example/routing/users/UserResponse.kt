package com.example.routing.users

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(val userId: String, val username: String, val email: String, val profilePicture: String)