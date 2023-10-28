package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.infraestructure.repository.CRUDRepository

class GetAllProjectsService(
    private val projectRepository: CRUDRepository<Entity.ProjectEntity>
) {
    suspend operator fun invoke(
        userId: String
    ): Result<List<Entity.ProjectEntity>> {
        val result = projectRepository.getAll()
        result.onSuccess { projectEntities ->
            return Result.success(projectEntities.filter { it.createdBy.userId.toString() == userId })
        }
        return Result.failure(Exception("unexpected exception"))
    }
}