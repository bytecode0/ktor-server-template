package com.example.domain.entities

import java.util.UUID

sealed class Entity(
    open val entityId: UUID,
    open val createdAt: Long
) {
    data class ProjectEntity(
        override val entityId: UUID,
        override val createdAt: Long,
        internal val createdBy: UserEntity,
        internal val title: String,
        internal val description: String,
        internal val members: List<UserEntity> = listOf(),
        internal val tasks: List<TaskEntity> = listOf()
    ): Entity(entityId, createdAt)

    data class TaskEntity(
        override val entityId: UUID,
        override val createdAt: Long,
        internal val completionAt: Long,
        internal val deadline: Long,
        internal val userId: UUID,
        internal val assignedTo: UUID,
        internal val title: String,
        internal val description: String,
        internal val priority: Priority,
        internal val status: Status,
        internal val subTasks: List<SubTaskEntity> = listOf(),
        internal val comments: List<CommentEntity> = listOf()
    ): Entity(entityId, createdAt)

    data class SubTaskEntity(
        override val entityId: UUID,
        override val createdAt: Long,
        private val subTaskId: Int,
        private val taskId: Int,
        private val assignedTo: Int,
        private val content: String,
        private val comments: List<CommentEntity> = listOf()
    ): Entity(entityId, createdAt)

    data class CommentEntity(
        override val entityId: UUID,
        override val createdAt: Long,
        private val taskId: Int,
        private val userId: Int,
        private val content: String
    ): Entity(entityId, createdAt)
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

enum class Priority { High, Low, Medium, None }

enum class Status { Completed, InProgress, OnHold, Canceled }

enum class NotificationStatus { Read, Unread }
