package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.domain.entities.UserEntity
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.CRUDRepository
import com.example.infraestructure.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class UpdateProjectServiceTest {
    private val users = mutableListOf<UserEntity>()
    private val projects = mutableListOf<Entity.ProjectEntity>()

    private val eventBus = mockk<EventBus>()

    private val userRepository = UserRepository(users)
    private val projectRepository = CRUDRepository(projects)

    private val createUserService = CreateUserService(userRepository, eventBus)
    private val createProjectService = CreateProjectService(userRepository, projectRepository, eventBus)
    private val updateProjectService = UpdateProjectService(projectRepository, eventBus)

    private val userEntity = UserEntity(
        userId = UUID.fromString("e88109e8-fc75-4cc5-91d9-0e0a2557bfac"),
        username = "vespasoft",
        email = "vespasoft@gmail.com",
        password = "1234",
        profilePicture = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"
    )

    private val projectEntity = Entity.ProjectEntity(
        entityId = UUID.fromString("c3de8f42-e28e-48a7-8bf4-affc82b74a3c"),
        createdAt = System.currentTimeMillis(),
        createdBy = userEntity,
        title = "Project 000001",
        description = "This is my favorite project"
    )

    @Test
    fun `Returns success result when update project service successfully`() = runTest{
        // -- Create User --
        coEvery { eventBus.publish(any()) } returns Unit

        val result = createUserService.invoke(
            username = "vespasoft",
            email = "vespasoft@gmail.com",
            password = "1m4*5Aa78@"
        )

        assert(result.isSuccess)
        result.onSuccess { userEntity ->
            assertEquals("vespasoft", userEntity.username)
            assertEquals("vespasoft@gmail.com", userEntity.email)
            // -- Create Project by User --
            createProjectService.invoke(
                createdBy = userEntity.userId.toString(),
                title = "Project 000001",
                description = "This is my favorite project",
                members = listOf(),
                tasks = listOf()
            ).onSuccess { projectEntity ->
                assertEquals(userEntity.userId, projectEntity.createdBy.userId)
                assertEquals("This is my favorite project", projectEntity.description)
                // -- Update Project
                updateProjectService.invoke(
                    projectId = projectEntity.entityId.toString(),
                    title = "Project 000001",
                    description = "This is my favorite project updated",
                    members = listOf(),
                    tasks = listOf()
                ).onSuccess {
                    assertEquals("This is my favorite project updated", projectEntity.description)
                }
            }
        }
    }

}