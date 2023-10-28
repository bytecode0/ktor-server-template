package com.example.routing

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionResponse(val errorCode: Int, val errorMessage: String)