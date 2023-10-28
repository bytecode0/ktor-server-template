package com.example

import com.example.domain.services.CreateUserService
import com.example.domain.services.UpdateUserPasswordService
import com.example.infraestructure.eventbus.EventBus
import com.example.infraestructure.repository.UserRepository
import com.example.plugins.*
import com.example.routing.configureRouting
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

    configureSerialization()
    configureSecurity()
    configureRouting()
    configureUsersRouting(createUserService, updateUserPasswordService)
}
