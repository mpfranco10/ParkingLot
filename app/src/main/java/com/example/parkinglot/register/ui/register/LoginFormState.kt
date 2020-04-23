package com.example.parkinglot.register.ui.register

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val firstError: Int? = null,
    val lastError: Int? = null,
    val phoneError: Int? = null,
    val isDataValid: Boolean = false
)
