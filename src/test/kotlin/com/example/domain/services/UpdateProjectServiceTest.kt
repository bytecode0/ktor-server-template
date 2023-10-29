package com.example.domain.services

import com.example.domain.entities.Entity
import com.example.domain.entities.UserEntity
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.CRUDRepository
import com.example.infraestructure.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
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