package com.example.domain.services

import com.example.domain.entities.UserEntity
import com.example.domain.events.Event
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.UserRepository
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

class CreateUserService(
    private val userRepository: UserRepository,
    private val eventBus: EventBus
) {
    suspend operator fun invoke(
        username: String, email: String, password: String, profilePicture: String? = null
    ): Result<UserEntity> {
        if (username.isEmpty()) {
            return Result.failure(Exception("Username must not be empty"))
        }

        if (email.isEmpty()) {
            return Result.failure(Exception("Email must not be empty"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email is not valid"))
        }

        if (!isValidPassword(password)) {
            return Result.failure(Exception("Password is not secured. It must has minimum length 6 characters and " +
                    "It must contains at least one letter, one number and one symbol."))
        }

        val newUser = UserEntity(
            userId = UUID.randomUUID(),
            username = username,
            email = email,
            password = password,
            profilePicture = profilePicture ?: "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png")
        val actionResult = userRepository.saveUser(newUser)
        if (actionResult.isSuccess) {
            val userCreatedEvent = Event.UserCreatedEvent(newUser.userId, newUser.username, newUser.email)
            eventBus.publish(userCreatedEvent)
        }

        return actionResult
    }

    // TODO: Consider using a password hashing library like BCrypt
    private fun hashAndSaltPassword(password: String): Pair<String, String> {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)

        val hashedPassword = md.digest(password.toByteArray(Charsets.UTF_8))
        val encodedHash = bytesToHex(hashedPassword)

        val encodedSalt = bytesToHex(salt)

        return Pair(encodedHash, encodedSalt)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder(2 * bytes.size)
        for (byte in bytes) {
            val hex = Integer.toHexString(byte.toInt() and 0xFF)
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)([.])(.+)"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        // Minimum length is 6 characters
        // Contains at least one letter, one number, and one symbol
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,}$"
        return password.matches(passwordRegex.toRegex())
    }
}