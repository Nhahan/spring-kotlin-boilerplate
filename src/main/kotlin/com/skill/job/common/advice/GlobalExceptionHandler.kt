package com.skill.job.common.advice

import com.skill.job.common.exception.AppException
import com.skill.job.common.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AppException::class)
    fun handleCustomException(ex: AppException): ResponseEntity<ErrorResponse> {
        val response =
            ErrorResponse(
                status = ex.status,
                error = HttpStatus.valueOf(ex.status).reasonPhrase,
                message = ex.message
            )
        return ResponseEntity(response, HttpStatus.valueOf(ex.status))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val response =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = ex.message ?: "Internal server error"
            )
        return ResponseEntity(response, status)
    }
}
