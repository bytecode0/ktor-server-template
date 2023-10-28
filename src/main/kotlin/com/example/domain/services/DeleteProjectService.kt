package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.infraestructure.repository.CRUDRepository
import java.util.*

class DeleteProjectService(
    private val projectRepository: CRUDRepository<Entity.ProjectEntity>
) {
    suspend fun invoke(
        projectId: String
    ): Result<Unit> {
        return projectRepository.remove(UUID.fromString(projectId))
    }
}
