package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.domain.entities.UserEntity
import com.example.domain.exceptions.ProjectException
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.CRUDRepository
import java.util.*

class UpdateProjectService(
    private val projectRepository: CRUDRepository<Entity.ProjectEntity>,
    private val eventBus: EventBus
) {
    suspend fun invoke(
        projectId: String,
        title: String,
        description: String,
        members: List<UserEntity>,
        tasks: List<Entity.TaskEntity>
    ): Result<Entity.ProjectEntity> {
        projectRepository.getById(UUID.fromString(projectId))
            .onSuccess { project ->
                val updatedProject = project.copy(
                    title = title,
                    description = description,
                    members = members,
                    tasks = tasks
                )
                return projectRepository.update(updatedProject)
            }

        return Result.failure(ProjectException(errorCode = 400, "the project Id is not valid or not exits!"))
    }
}
