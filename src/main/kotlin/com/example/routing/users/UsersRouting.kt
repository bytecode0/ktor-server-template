package com.example.routing.users

import com.example.domain.exceptions.UserException
import com.example.domain.services.CreateUserService
import com.example.domain.services.UpdateUserPasswordService
import com.example.routing.ExceptionResponse
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

fun Application.configureUsersRouting(
    createUserService: CreateUserService,
    updateUserPasswordService: UpdateUserPasswordService
) {
    routing {
        route("/api/v1/users") {
            post {
                try {
                    val user = call.receive<CreateUserRequest>()

                    val result = runBlocking {
                        createUserService.invoke(
                            username = user.username,
                            email = user.email,
                            password = user.password
                        )
                    }
                    result.onSuccess {
                        call.respond(
                            Created,
                            UserResponse(
                                it.userId.toString(),
                                it.username,
                                it.email,
                                it.profilePicture
                            )
                        )
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode == 409) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, it)
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("UserRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, e.localizedMessage)
                }
            }
            put {
                try {
                    val user = call.receive<UpdatePasswordRequest>()

                    val result = runBlocking {
                        updateUserPasswordService.invoke(
                            userId = user.userId,
                            currentPassword = user.currentPassword,
                            newPassword = user.newPassword
                        )
                    }
                    result.onSuccess {
                        call.respond(Created, "User password has been updated successfully")
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode == 409) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, it)
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("UserRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, e.localizedMessage)
                }
            }
        }
    }
}

@Serializable
data class UserResponse(
    val userId: String,
    val username: String,
    val email: String,
    val profilePicture: String
)

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UpdatePasswordRequest(
    val userId: String,
    val currentPassword: String,
    val newPassword: String
)
