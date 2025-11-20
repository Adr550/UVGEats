package com.uvg.uvgeats.ui.login

//Estado inmutable data class
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// eventos de ui sellados
sealed interface LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent
    data class PasswordChanged(val password: String) : LoginUiEvent
    data object LoginClicked : LoginUiEvent
    data object ForgotPasswordClicked : LoginUiEvent
}

// efectos de ui sellados
sealed interface LoginUiEffect {
    data object NavigateToHome : LoginUiEffect
    data object NavigateToForgotPassword : LoginUiEffect
    data class ShowError(val message: String) : LoginUiEffect
}