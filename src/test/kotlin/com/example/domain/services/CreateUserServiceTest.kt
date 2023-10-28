package com.example.domain.services

import com.example.domain.entities.UserEntity
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class CreateUserServiceTest {
    private val eventBus = mockk<EventBus>()
    private val userRepositoryMock = mockk<UserRepository>()
    private val userEntity = UserEntity(
        userId = UUID.randomUUID(),
        username = "vespasoft",
        email = "vespasoft@gmail.com",
        password = "1234",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )

    @Test
    fun `Returns success result when creates user service is successful`() = runTest {
        val createUserService = CreateUserService(userRepositoryMock, eventBus)

        coEvery { eventBus.publish(any()) } returns Unit
        coEvery { userRepositoryMock.saveUser(any()) } returns Result.success(userEntity)

        val result = createUserService.invoke(
            username = "vespasoft",
            email = "vespasoft@gmail.com",
            password = "1m4*5Aa78@"
        )

        assert(result.isSuccess)
        result.onSuccess {
            assertEquals("vespasoft", it.username)
            assertEquals("vespasoft@gmail.com", it.email)
        }
        coVerify(exactly = 1) { eventBus.publish(any()) }
    }

    @Test
    fun `Returns failure result when username is empty`() = runTest {
        val createUserService = CreateUserService(userRepositoryMock, eventBus)

        val expected = "Username must not be empty"
        val result = createUserService.invoke(
            username = "",
            email = "vespasoft@gmail.com",
            password = "1m4-5Aaa78@"
        )

        assert(result.isFailure)
        result.onFailure {  actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `Returns failure result when email is empty`() = runTest {
        val createUserService = CreateUserService(userRepositoryMock, eventBus)

        val expected = "Email must not be empty"
        val result = createUserService.invoke(
            username = "vespasoft",
            email = "",
            password = "1m4-5Aaa78@"
        )

        assert(result.isFailure)
        result.onFailure {  actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `Returns failure result when email is not valid`() = runTest {
        val createUserService = CreateUserService(userRepositoryMock, eventBus)

        val expected = "Email is not valid"
        val result = createUserService.invoke(
            username = "vespasoft",
            email = "vespaso",
            password = "1m4-5Aaa78@"
        )

        assert(result.isFailure)
        result.onFailure {  actual ->
            assertEquals(expected, actual.message)
        }
    }

    @Test
    fun `Returns failure result when password is not secured`() = runTest {
        val createUserService = CreateUserService(userRepositoryMock, eventBus)

        val expected = "Password is not secured. It must has minimum length 6 characters and It must contains at least one letter, one number and one symbol."
        val result = createUserService.invoke(
            username = "vespasoft",
            email = "vespasoft@gmail.com",
            password = "123456"
        )

        assert(result.isFailure)
        result.onFailure {  actual ->
            assertEquals(expected, actual.message)
        }
    }
}