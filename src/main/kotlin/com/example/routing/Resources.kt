package com.example.routing

import io.ktor.resources.*
import kotlinx.serialization.Serializable

/*** CUSTOM RESOURCES ***/
@Serializable
data class ExceptionResponse(val errorCode: Int, val errorMessage: String)

/*** USERS RESOURCES ***/
@Serializable
data class UserResponse(
    val userId: String,
    val username: String,
    val email: String,
    val profilePicture: String
)

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

@Serializable
class ProjectRequest(
    val title: String,
    val description: String,
    val tasks: List<TaskRequest>
)

@Serializable
class ProjectPutRequest(
    val title: String,
    val description: String,
    val tasks: List<TaskPutRequest>
)

@Serializable
data class ProjectResponse(
    val projectId: String,
    val title: String,
    val description: String,
    val tasks: List<TaskResponse> = listOf()
)

@Serializable
data class TaskRequest(
    val completionAt: Long,
    val deadline: Long,
    val userId: String,
    val assignedTo: String,
    val title: String,
    val description: String,
    val priority: Int,
    val status: Int,
)

@Serializable
data class TaskPutRequest(
    val taskId: String,
    val completionAt: Long,
    val deadline: Long,
    val userId: String,
    val assignedTo: String,
    val title: String,
    val description: String,
    val priority: Int,
    val status: Int,
)

@Serializable
data class TaskResponse(
    val taskId: String,
    val completionAt: Long,
    val deadline: Long,
    val userId: String,
    val assignedTo: String,
    val title: String,
    val description: String,
    val priority: Int,
    val status: Int,
)

@Serializable
@Resource("/projects")
data class Projects(val projects: List<ProjectResponse> = listOf())