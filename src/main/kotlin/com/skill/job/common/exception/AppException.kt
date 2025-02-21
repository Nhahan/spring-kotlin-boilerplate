package com.skill.job.common.exception

class AppException(
    val status: Int,
    override val message: String
) : RuntimeException(message)
