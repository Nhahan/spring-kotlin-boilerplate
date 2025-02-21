package org.example.statelessspringsecurity.enums

import com.skill.job.common.exception.AppException
import org.springframework.http.HttpStatus

enum class MemberRole {
    ADMIN,
    MEMBER,
    CORPORATE_MEMBER
    ;

    companion object {
        fun of(role: String): MemberRole {
            return try {
                valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                throw AppException(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid member role: $role"
                )
            }
        }
    }
}
