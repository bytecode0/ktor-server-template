package com.example.domain.entities

import java.util.UUID

sealed class Entity(
    open val createdAt: Long
) {
    data class ProjectEntity(
        private val projectId: Int,
        override val createdAt: Long,
        private val createdBy: com.example.domain.entities.UserEntity,
        private val title: String,
        private val description: String,
        private val members: List<com.example.domain.entities.UserEntity> = listOf(),
        private val tasks: List<TaskEntity> = listOf()
    ): Entity(createdAt)

    data class TaskEntity(
        private val taskId: Int,
        override val createdAt: Long,
        private val completionAt: Long,
        private val deadline: Long,
        private val createdBy: com.example.domain.entities.UserEntity,
        private val assignedTo: Int,
        private val title: String,
        private val description: String,
        private val priority: Priority,
        private val status: Status,
        private val subTasks: List<SubTaskEntity> = listOf(),
        private val comments: List<CommentEntity> = listOf()
    ): Entity(createdAt)

    data class SubTaskEntity(
        private val subTaskId: Int,
        override val createdAt: Long,
        private val taskId: Int,
        private val assignedTo: Int,
        private val content: String,
        private val comments: List<CommentEntity> = listOf()
    ): Entity(createdAt)

    data class CommentEntity(
        private val commentId: Int,
        override val createdAt: Long,
        private val taskId: Int,
        private val userId: Int,
        private val content: String
    ): Entity(createdAt)
}

data class UserEntity(
    internal val userId: UUID,
    internal val email: String,
    internal val password: String, // hashed and salted
    val username: String,
    val profilePicture: String
)

data class NotificationEntity(
    private val notificationId: Int,
    private val sentAt: Long,
    private val recipient: UserEntity,
    private val content: String,
    private val status: NotificationStatus,
    private val entityRelated: Entity
)

enum class Priority { High, Low, OnHold }

enum class Status { Completed, InProgress, OnHold, Canceled }

enum class NotificationStatus { Read, Unread }
