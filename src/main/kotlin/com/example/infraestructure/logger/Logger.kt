package com.example.infraestructure.logger

import java.util.logging.Logger


object Logger {
    fun logInfo(clazz: Class<*>, message: String) {
        Logger.getLogger(clazz.name).info(message)
    }

    fun logError(clazz: Class<*>, message: String, throwable: Throwable) {
        Logger.getLogger(clazz.name).warning(message)
        Logger.getLogger(clazz.name).warning(throwable.message)
    }
}