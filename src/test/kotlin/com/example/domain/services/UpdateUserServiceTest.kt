package com.example.domain.services

import com.example.domain.entities.UserEntity
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.Exception
import java.util.*
import kotlin.test.assertEquals

class UpdateUserServiceTest {
    private val eventBus = mockk<EventBus>()
    private val userRepositoryMock = mockk<UserRepository>()
    private val userEntity = UserEntity(
        userId = UUID.randomUUID(),
        username = "vespasoft",
        email = "vespasoft@gmail.com",
        password = "1m4*5Aa78@",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )

    @Test
    fun `Returns success result when creates user service is successful`() = runTest {
        val updateUserPasswordService = UpdateUserPasswordService(userRepositoryMock, eventBus)

        coEvery { eventBus.publish(any()) } returns Unit
        coEvery { userRepositoryMock.getUserById(any()) } returns Result.success(userEntity)
        coEvery { userRepositoryMock.updateUser(any()) } returns Result.success(userEntity)

        val result = updateUserPasswordService.invoke(
            userId = UUID.randomUUID().toString(),
            currentPassword = "1m4*5Aa78@",
            newPassword = "1m4*5Aa00@"
        )


        coVerify(exactly = 1) { userRepositoryMock.updateUser(any()) }
        coVerify(exactly = 1) { eventBus.publish(any()) }
        assert(result.isSuccess)
    }

    @Test
    fun `Returns failure result when update user password service with a wrong current password`() = runTest {
        val updateUserPasswordService = UpdateUserPasswordService(userRepositoryMock, eventBus)

        coEvery { userRepositoryMock.getUserById(any()) } returns Result.success(userEntity)

        val result = updateUserPasswordService.invoke(
            userId = UUID.randomUUID().toString(),
            currentPassword = "1m4*5Aa00@",
            newPassword = "1m4*5Aa11@"
        )

        coVerify(exactly = 0) { userRepositoryMock.updateUser(any()) }
        coVerify(exactly = 0) { eventBus.publish(any()) }
        assert(result.isFailure)
        result.onFailure {
            val expected = "current password provided is not correct"
            assertEquals(expected, it.message)
        }
    }

    @Test
    fun `Returns failure result when update user password service with a wrong userId`() = runTest {
        val updateUserPasswordService = UpdateUserPasswordService(userRepositoryMock, eventBus)

        coEvery { userRepositoryMock.getUserById(any()) } returns Result.failure(Exception("userId does not exits"))

        val result = updateUserPasswordService.invoke(
            userId = UUID.randomUUID().toString(),
            currentPassword = "1m4*5Aa00@",
            newPassword = "1m4*5Aa01@"
        )

        coVerify(exactly = 0) { userRepositoryMock.updateUser(any()) }
        coVerify(exactly = 0) { eventBus.publish(any()) }
        assert(result.isFailure)
        result.onFailure {
            val expected = "userId is incorrect or it does not exits"
            assertEquals(expected, it.message)
        }
    }

    @Test
    fun `Returns failure result when update user password service with equal passwords`() = runTest {
        val updateUserPasswordService = UpdateUserPasswordService(userRepositoryMock, eventBus)

        coEvery { userRepositoryMock.getUserById(any()) } returns Result.failure(Exception("userId does not exits"))

        val result = updateUserPasswordService.invoke(
            userId = UUID.randomUUID().toString(),
            currentPassword = "1m4*5Aa00@",
            newPassword = "1m4*5Aa00@"
        )

        coVerify(exactly = 0) { userRepositoryMock.updateUser(any()) }
        coVerify(exactly = 0) { eventBus.publish(any()) }
        assert(result.isFailure)
        result.onFailure {
            val expected = "It's not possible update equal passwords"
            assertEquals(expected, it.message)
        }
    }
}