package com.example.studentlibrary.ui.auth

enum class UserRole {
    USER,
    ADMIN,
}

data class AuthSession(
    val role: UserRole,
)

object DemoCredentials {
    const val UserPassword = "student123"
    const val AdminPassword = "admin123"
}
