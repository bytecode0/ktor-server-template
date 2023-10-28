package com.example.infraestructure.repository

import com.example.domain.entities.UserEntity
import com.example.domain.exceptions.UserException
import com.example.infraestructure.logger.Logger
import java.lang.Exception
import java.util.UUID

class ProjectRepository(
    private val projects: MutableList<UserEntity> = mutableListOf()
) {

    suspend fun saveProject(userEntity: UserEntity): Result<UserEntity> {
        return try {
            if (projects.firstOrNull { it.email == userEntity.email || it.username == userEntity.username } != null) {
                Result.failure(UserException(errorCode = 409, errorMessage = "the user already exits"))
            } else {
                projects.add(userEntity)
                Logger.logInfo(this::class.java, "User created => ${userEntity.userId}, ${userEntity.username}, ${userEntity.email}  ")
                Result.success(userEntity)
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error saving user", e)
            Result.failure(UserException(errorCode = 500, errorMessage = "Error saving user"))
        }
    }

    suspend fun updateProject(userEntity: UserEntity): Result<UserEntity> {
        return try {
            val existingUserIndex = projects.indexOfFirst { it.userId == userEntity.userId }
            if (existingUserIndex != -1) {
                projects[existingUserIndex] = userEntity
                Logger.logInfo(this::class.java, "User updated => ${userEntity.userId}, ${userEntity.username}, ${userEntity.email}  ")
                Result.success(userEntity)
            } else {
                Result.failure(Exception("It does not exits any user with the userId ${userEntity.userId}"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error updating user", e)
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: UUID): Result<UserEntity> {
        return try {
            projects.firstOrNull { it.userId == userId }?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("It does not exits any user with the userId $userId"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error getting user by Id", e)
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        return try {
            projects.firstOrNull { it.email == email }?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("It does not exits any user with the email $email"))
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error getting user by email", e)
            Result.failure(e)
        }
    }

}
