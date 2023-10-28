package com.example.routing.users

import com.example.domain.entities.UserEntity
import com.example.domain.services.CreateUserService
import com.example.domain.services.UpdateUserPasswordService
import com.example.routing.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UsersRoutingKtTest {
    @Test
    fun `Users route post creates an user and return success`() = runTest {
        testApplication {
            // Mock your service and repository
            val createUserService = mockk<CreateUserService>()
            val updateUserPasswordService = mockk<UpdateUserPasswordService>()

            // Stub the service method
            coEvery { createUserService.invoke(any(), any(), any()) } returns Result.success(
                UserEntity(
                    userId = UUID.randomUUID(),
                    username = "username",
                    email = "email",
                    password = "password",
                    profilePicture = ""
                )
            )

            application {
                configureRouting()
                configureUsersRouting(createUserService, updateUserPasswordService)
            }
            val jsonData = """
                {
                    "username": "example",
                    "email": "example@example.com",
                    "password": "password123"
                }
            """.trimIndent()
            client.post {
                url("/api/v1/users")
                headers {
                    accept(ContentType.Application.Json)
                }
                setBody(jsonData)
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Hello World!", bodyAsText())
            }
        }
    }
}