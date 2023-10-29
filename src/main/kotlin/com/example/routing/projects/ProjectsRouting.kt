package com.example.routing.projects

import com.example.domain.entities.Entity
import com.example.domain.entities.Priority
import com.example.domain.entities.Status
import com.example.domain.exceptions.UserException
import com.example.domain.services.CreateProjectService
import com.example.domain.services.DeleteProjectService
import com.example.domain.services.GetAllProjectsService
import com.example.domain.services.UpdateProjectService
import com.example.routing.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import java.util.UUID

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
                                        description = project.description,
                                        tasks = project.tasks.map {
                                            TaskResponse(
                                                taskId = it.entityId.toString(),
                                                completionAt = it.completionAt,
                                                deadline = it.deadline,
                                                userId = it.userId.toString(),
                                                assignedTo = it.assignedTo.toString(),
                                                title = it.title,
                                                description = it.description,
                                                priority = it.priority.ordinal,
                                                status = it.status.ordinal
                                            )
                                        }
                                    )
                                }
                            )
                        )
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode in 400..499) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, ExceptionResponse(InternalServerError.value, it.localizedMessage))
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, ExceptionResponse(BadRequest.value, e.localizedMessage))
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
                                tasks = project.tasks.map {  task ->
                                    Entity.TaskEntity(
                                        entityId = UUID.randomUUID(),
                                        createdAt = System.currentTimeMillis(),
                                        completionAt = task.completionAt,
                                        deadline = task.deadline,
                                        userId = UUID.fromString(task.userId),
                                        assignedTo = UUID.fromString(task.assignedTo),
                                        title = task.title,
                                        description = task.description,
                                        priority = when (task.priority) {
                                            0 -> Priority.Low
                                            1 -> Priority.Medium
                                            2 -> Priority.High
                                            else -> {
                                                Priority.High
                                            }
                                        },
                                        status = when (task.status) {
                                            0 -> Status.InProgress
                                            1 -> Status.Canceled
                                            2 -> Status.InProgress
                                            else -> {
                                                Status.OnHold
                                            }
                                        },
                                        subTasks = listOf(),
                                        comments = listOf()
                                    )
                                }
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
                        if (it is UserException && it.errorCode in 400..499) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, ExceptionResponse(InternalServerError.value, it.localizedMessage))
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, ExceptionResponse(BadRequest.value, e.localizedMessage))
                }
            }
        }
        route("/api/v1/projects/{projectId}") {
            put {
                try {
                    val projectId: String = call.parameters["projectId"]
                        ?: return@put call.respond(BadRequest, "Invalid project ID")

                    val project = call.receive<ProjectPutRequest>()
                    val result = runBlocking {
                        updateProjectService.invoke(
                            projectId = projectId,
                            title = project.title,
                            description = project.description,
                            members = listOf(),
                            tasks = project.tasks?.map {  task ->
                                Entity.TaskEntity(
                                    entityId = UUID.fromString(task.taskId),
                                    createdAt = System.currentTimeMillis(),
                                    completionAt = task.completionAt,
                                    deadline = task.deadline,
                                    userId = UUID.fromString(task.userId),
                                    assignedTo = UUID.fromString(task.assignedTo),
                                    title = task.title,
                                    description = task.description,
                                    priority = when (task.priority) {
                                        0 -> Priority.Low
                                        1 -> Priority.Medium
                                        2 -> Priority.High
                                        else -> {
                                            Priority.High
                                        }
                                    },
                                    status = when (task.status) {
                                        0 -> Status.InProgress
                                        1 -> Status.Canceled
                                        2 -> Status.InProgress
                                        else -> {
                                            Status.OnHold
                                        }
                                    },
                                    subTasks = listOf(),
                                    comments = listOf()
                                )
                            } ?: listOf()
                        )
                    }
                    result.onSuccess {
                        call.respond(OK, "Project has been updated successfully")
                    }
                    result.onFailure {
                        if (it is UserException && it.errorCode in 400..499) {
                            call.respond(Conflict, ExceptionResponse(it.errorCode, it.errorMessage))
                        } else {
                            call.respond(InternalServerError, ExceptionResponse(InternalServerError.value, it.localizedMessage))
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, ExceptionResponse(BadRequest.value, e.localizedMessage))
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
                            call.respond(InternalServerError, ExceptionResponse(InternalServerError.value, it.localizedMessage))
                        }
                    }
                } catch (e: Exception) {
                    java.util.logging.Logger.getLogger("ProjectRouting").warning(e.localizedMessage)
                    call.respond(BadRequest, ExceptionResponse(BadRequest.value, e.localizedMessage))
                }
            }
        }
    }
}

