package com.example

import com.example.domain.entities.Entity
import com.example.domain.services.*
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.CRUDRepository
import com.example.infraestructure.repository.UserRepository
import com.example.plugins.*
import com.example.routing.configureRouting
import com.example.routing.projects.configureProjectsRouting
import com.example.routing.users.configureUsersRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val eventBus = EventBus()
    val userRepository = UserRepository(mutableListOf())
    val createUserService = CreateUserService(userRepository, eventBus)
    val updateUserPasswordService = UpdateUserPasswordService(userRepository, eventBus)
    val projectRepository: CRUDRepository<Entity.ProjectEntity> = CRUDRepository(mutableListOf())
    val createProjectService = CreateProjectService(userRepository, projectRepository, eventBus)
    val getAllProjectService = GetAllProjectsService(projectRepository)
    val updateProjectService = UpdateProjectService(projectRepository, eventBus)
    val deleteProjectService = DeleteProjectService(projectRepository)

    configureSerialization()
    configureSecurity()
    configureRouting()
    configureUsersRouting(createUserService, updateUserPasswordService)
    configureProjectsRouting(createProjectService, getAllProjectService, updateProjectService, deleteProjectService)
}
