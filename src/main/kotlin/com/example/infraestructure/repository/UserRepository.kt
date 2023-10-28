package com.example.infraestructure.repository

import com.example.domain.entities.UserEntity
import com.example.domain.exceptions.UserException
import com.example.infraestructure.logger.Logger
import java.lang.Exception
import java.util.UUID

class UserRepository(
    private val users: MutableList<UserEntity> = mutableListOf()
) {

    suspend fun saveUser(userEntity: UserEntity): Result<UserEntity> {
        return try {
            if (users.firstOrNull { it.email == userEntity.email || it.username == userEntity.username } != null) {
                Result.failure(UserException(errorCode = 409, errorMessage = "the user already exits"))
            } else {
                users.add(userEntity)
                Logger.logInfo(this::class.java, "User created => ${userEntity.userId}, ${userEntity.username}, ${userEntity.email}  ")
                Result.success(userEntity)
            }
        } catch (e: Exception) {
            Logger.logError(this::class.java, "Error saving user", e)
            Result.failure(UserException(errorCode = 500, errorMessage = "Error saving user"))
        }
    }

    suspend fun updateUser(userEntity: UserEntity): Result<UserEntity> {
        return try {
            val existingUserIndex = users.indexOfFirst { it.userId == userEntity.userId }
            if (existingUserIndex != -1) {
                users[existingUserIndex] = userEntity
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
            users.firstOrNull { it.userId == userId }?.let {
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
            users.firstOrNull { it.email == email }?.let {
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
