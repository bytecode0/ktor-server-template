package com.example.domain.services

import com.example.domain.events.Event
import com.example.domain.exceptions.UserException
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.UserRepository
import java.util.UUID

class UpdateUserPasswordService(
    private val userRepository: UserRepository,
    private val eventBus: EventBus
) {
    suspend fun invoke(userId: String, currentPassword: String, newPassword: String): Result<Unit> {
        if (currentPassword == newPassword) {
            return Result.failure(UserException(errorCode = 409, "It's not possible update equal passwords"))
        }

        if (!isValidPassword(newPassword)) {
            return Result.failure(UserException(errorCode = 409, "Password is not secured. It must has minimum length 6 characters and " +
                    "It must contains at least one letter, one number and one symbol."))
        }

        userRepository.getUserById(UUID.fromString(userId))
            .onSuccess {
                if (it.password != currentPassword) {
                    return Result.failure(UserException(errorCode = 409, "current password provided is not correct"))
                }

                userRepository.updateUser(it.copy(password = newPassword))
                eventBus.publish(Event.UserPasswordUpdatedEvent(it.userId, it.username, it.email))
            }
            .onFailure {
                return Result.failure(UserException(errorCode = 400, "userId is incorrect or it does not exits"))
            }

        return Result.success(Unit)
    }

    private fun isValidPassword(password: String): Boolean {
        // Minimum length is 6 characters
        // Contains at least one letter, one number, and one symbol
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}$"
        return password.matches(passwordRegex.toRegex())
    }

}
