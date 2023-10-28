package com.example.infraestructure.repository

import com.example.domain.entities.Entity
import com.example.domain.exceptions.UserException
import com.example.infraestructure.logger.Logger
import java.util.UUID

class CRUDRepository<T : Entity>(
    private val collection: MutableList<T>
) {
    suspend fun save(entity: T): Result<T> {
        return try {
            collection.add(entity)
            Result.success(entity)
        } catch (e: Exception) {
            Logger.logError(this::class.java, "error saving entity", e)
            Result.failure(UserException(errorCode = 500, errorMessage = "Error saving entity"))
        }
    }

    suspend fun update(entity: T): Result<T> {
        return try {
            collection.firstOrNull { it.entityId == entity.entityId }?.let {
                collection[collection.indexOf(it)] = entity
                Result.success(entity)
            } ?: run {
                Result.failure(Exception("It does not exist any entity with the entityId ${entity.entityId}"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error updating entity", e)
            Result.failure(e)
        }
    }

    suspend fun getById(entityId: UUID): Result<T> {
        return try {
            collection.firstOrNull { it.entityId == entityId }?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("It does not exist any entity with the ID $entityId"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error getting entity by Id", e)
            Result.failure(e)
        }
    }

    suspend fun getAll(): Result<List<T>> {
        return try {
            Result.success(collection)
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error getting all entities", e)
            Result.failure(e)
        }
    }

    suspend fun remove(entityId: UUID): Result<Unit> {
        return try {
            collection.firstOrNull { it.entityId == entityId }?.let {
                collection.indexOfFirst { it.entityId == entityId }.let { index ->
                    collection.removeAt(index)
                    Result.success(Unit)
                }
            } ?: run {
                Result.failure(Exception("It does not exist any entity with the ID $entityId"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error removing entity by Id", e)
            Result.failure(e)
        }
    }
}
