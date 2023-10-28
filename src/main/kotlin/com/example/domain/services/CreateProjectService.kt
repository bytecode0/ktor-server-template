package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.domain.entities.UserEntity
import com.example.domain.events.Event
import com.example.domain.exceptions.ProjectException
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.CRUDRepository
import com.example.infraestructure.repository.UserRepository
import java.util.*

class CreateProjectService(
    private val userRepository: UserRepository,
    private val projectRepository: CRUDRepository<Entity.ProjectEntity>,
    private val eventBus: EventBus
) {
    suspend operator fun invoke(
        createdBy: String,
        title: String,
        description: String,
        members: List<UserEntity>,
        tasks: List<Entity.TaskEntity>
    ): Result<Entity.ProjectEntity> {
        userRepository.getUserById(UUID.fromString(createdBy))
            .onSuccess { user ->
                val newProject = Entity.ProjectEntity(
                    entityId = UUID.randomUUID(),
                    createdAt = System.currentTimeMillis(),
                    createdBy = user,
                    title = title,
                    description = description,
                    members = members,
                    tasks = tasks
                )
                val actionResult = projectRepository.save(newProject)
                if (actionResult.isSuccess) {
                    val userCreatedEvent = Event.ProjectCreatedEvent(user, newProject)
                    eventBus.publish(userCreatedEvent)
                }
                return actionResult
            }

        return Result.failure(ProjectException(errorCode = 400, "the user Id is not valid or not exits!"))
    }
}