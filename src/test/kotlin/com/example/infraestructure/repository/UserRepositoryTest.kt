package com.example.infraestructure.repository

import com.example.domain.entities.UserEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class UserRepositoryTest {
    private val users = mutableListOf<UserEntity>()
    private val userRepository: UserRepository = UserRepository(users)
    private val userEntity = UserEntity(
        userId = UUID.randomUUID(),
        username = "vespasoft",
        email = "vespasoft@gmail.com",
        password = "1234",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )
    private val user1 = UserEntity(
        userId = UUID.randomUUID(),
        username = "user1",
        email = "user1@gmail.com",
        password = "1234",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )
    private val user2 = UserEntity(
        userId = UUID.randomUUID(),
        username = "user2",
        email = "user2@gmail.com",
        password = "1234",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )

    @Test
    fun `saveUser should return success result when user has been saved`() = runTest {
        val result = userRepository.saveUser(userEntity)

        assert(result.isSuccess)
        result.onSuccess {
            assertEquals("vespasoft", it.username)
            assertEquals("vespasoft@gmail.com", it.email)
        }
    }

    @Test
    fun `saveUser should return failure result when user already exits`() = runTest {
        userRepository.saveUser(userEntity)

        val expected = "the user already exits"
        val result = userRepository.saveUser(userEntity)

        assert(result.isFailure)
        result.onFailure { actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `saveUser should return failure result when user has not been saved`() = runTest {
        val usersMock = mockk<MutableList<UserEntity>>()
        val userRepository = UserRepository(usersMock)

        coEvery { usersMock.add(any()) } throws Exception("error at try to save user")

        val result = userRepository.saveUser(userEntity)

        assert(result.isFailure)
    }

    @Test
    fun `updateUser should return success result when user has been updated`() = runTest {
        userRepository.saveUser(userEntity)

        val result = userRepository.updateUser(userEntity.copy(username = "updated"))

        assert(result.isSuccess)
    }

    @Test
    fun `updateUser should return failure result when user has not been updated`() = runTest {
        val usersMock = mockk<MutableList<UserEntity>>()
        val userRepository = UserRepository(usersMock)

        coEvery { usersMock.indexOfFirst { any() } } returns -1

        val result = userRepository.updateUser(userEntity)

        assert(result.isFailure)
    }

    @Test
    fun `updateUser should return failure result when userId does not exits`() = runTest {
        userRepository.saveUser(userEntity)

        val userToUpdate = userEntity.copy(userId = UUID.randomUUID(), username = "updated")
        val expected = "It does not exits any user with the userId ${userToUpdate.userId}"
        val result = userRepository.updateUser(userToUpdate)

        assert(result.isFailure)
        result.onFailure { actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `getUserById should return success with the expected user`() = runTest{
        val expected = user2
        userRepository.saveUser(
            UserEntity(
                userId = UUID.randomUUID(),
                username = "user1",
                email = "user1@gmail.com",
                password = "1234",
                profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
            )
        )
        userRepository.saveUser(
            expected
        )

        val result = userRepository.getUserById(userId = expected.userId)

        assert(result.isSuccess)
        result.onSuccess { actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `getUserById should return failure result if it throws an exception`() = runTest {
        val usersMock = mockk<MutableList<UserEntity>>()
        val userRepository = UserRepository(usersMock)

        coEvery { usersMock.firstOrNull { any() } } throws Exception("error at try to find user by Id")

        val expected = "error at try to find user by Id"
        val result = userRepository.getUserById(UUID.randomUUID())

        assert(result.isFailure)
        result.onFailure { actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `getUserById should return failure when it does not exits any user with the userId`() = runTest{
        val userId = UUID.randomUUID()
        val expected = "It does not exits any user with the userId $userId"
        userRepository.saveUser(user1)
        userRepository.saveUser(user2)

        val result = userRepository.getUserById(userId = userId)

        assert(result.isFailure)
        result.onFailure {
            assertEquals(expected, it.message)
        }
    }

    @Test
    fun `getUserByEmail should return success with the expected user`() = runTest{
        val expected = user2
        userRepository.saveUser(user1)
        userRepository.saveUser(expected)

        val result = userRepository.getUserByEmail("user2@gmail.com")

        assert(result.isSuccess)
        result.onSuccess { actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `getUserByEmail should return failure result if it throws an exception`() = runTest {
        val usersMock = mockk<MutableList<UserEntity>>()
        val userRepository = UserRepository(usersMock)

        coEvery { usersMock.firstOrNull { any() } } throws Exception("error at try to find user by email")

        val expected = "error at try to find user by email"
        val result = userRepository.getUserByEmail("email")

        assert(result.isFailure)
        result.onFailure {
            assertEquals(expected, it.message)
        }
    }

    @Test
    fun `getUserByEmail should return failure when it does not exits any user with the email`() = runTest{
        val expected = "It does not exits any user with the email email"
        userRepository.saveUser(user1)
        userRepository.saveUser(user2)
        val result = userRepository.getUserByEmail("email")

        assert(result.isFailure)
        result.onFailure {
            assertEquals(expected, it.message)
        }
    }
}