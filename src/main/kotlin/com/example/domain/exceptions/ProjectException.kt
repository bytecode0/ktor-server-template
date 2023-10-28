package com.example.domain.exceptions

import java.lang.Exception

class ProjectException(
    val errorCode: Int = 0,
    val errorMessage: String
): Exception(errorMessage)