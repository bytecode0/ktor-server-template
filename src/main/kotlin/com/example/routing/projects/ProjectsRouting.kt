package com.example.routing.projects

import com.example.domain.exceptions.ProjectException
import com.example.domain.exceptions.UserException
import com.example.domain.services.CreateProjectService
import com.example.domain.services.DeleteProjectService
import com.example.domain.services.GetAllProjectsService
import com.example.domain.services.UpdateProjectService
import com.example.routing.ExceptionResponse
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

fun Application.configureProjectsRouting(
    createProjectService: CreateProjectService,
    getAllProjectsService: GetAllProjectsService,
    updateProjectService: UpdateProjectService,
    deleteProjectService: DeleteProjectService
) {
    routing {
        route("/api/v1/users/{userId}/projects") {
            get {
                try {
                    val result = runBlocking {
                        call.parameters["userId"].let {
                            getAllProjectsService.invoke(userId = it.toString())
                        }
                    }
                    result.onSuccess {
                        call.respond(
                            OK,
                            Projects(
                                projects = it.map { project ->
                                    ProjectResponse(
                                        projectId = project.entityId.toString(),
                                        title = project.title,
                                        description = project.description
                                    )
                                }
                            )
                        )
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode in 400..499) {
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
            post {
                try {
                    val project = call.receive<ProjectRequest>()
                    val result = runBlocking {
                        call.parameters["userId"].let {
                            createProjectService.invoke(
                                createdBy = it.toString(),
                                title = project.title,
                                description = project.description,
                                members = listOf(),
                                tasks = listOf()
                            )
                        }
                    }
                    result.onSuccess {
                        call.respond(
                            Created,
                            ProjectResponse(
                                projectId = it.entityId.toString(),
                                title = it.title,
                                description = it.description
                            )
                        )
                    }
                    result.onFailure {
                        if (it is ProjectException && it.errorCode in 400..499) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, it.localizedMessage)
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectsRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, e.localizedMessage)
                }
            }
        }
        route("/api/v1/projects/{projectId}") {
            put {
                try {
                    val projectId: String = call.parameters["projectId"]
                        ?: return@put call.respond(BadRequest, "Invalid project ID")

                    val project = call.receive<ProjectRequest>()
                    val result = runBlocking {
                        updateProjectService.invoke(
                            projectId = projectId,
                            title = project.title,
                            description = project.description,
                            members = listOf(),
                            tasks = listOf()
                        )
                    }
                    result.onSuccess {
                        call.respond(HttpStatusCode.OK, "Project has been updated successfully")
                    }
                    result.onFailure {
                        throw it // Let StatusPages handle the exception based on the type
                    }
                } catch (e: Exception) {
                    call.application.environment.log.error("Error processing request", e)
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }
            delete {
                try {
                    val result = runBlocking {
                        call.parameters["projectId"].let {
                            deleteProjectService.invoke(it.toString())
                        }
                    }
                    result.onSuccess {
                        call.respond(OK, "Project has been deleted successfully")
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode in 400..499) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, it)
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, e.localizedMessage)
                }
            }
        }
    }
}

@Serializable
class ProjectRequest(
    val title: String,
    val description: String
)

@Serializable
data class ProjectResponse(
    val projectId: String,
    val title: String,
    val description: String
)

@Serializable
@Resource("/projects")
class Projects(val projects: List<ProjectResponse> = listOf())
