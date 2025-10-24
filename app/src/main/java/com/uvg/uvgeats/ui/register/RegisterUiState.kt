package com.uvg.uvgeats.ui.register

// UiState inmutable
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val passwordsMatch: Boolean = true
)

// Eventos de UI
sealed interface RegisterUiEvent {
    data class EmailChanged(val email: String) : RegisterUiEvent
    data class PasswordChanged(val password: String) : RegisterUiEvent
    data class RepeatPasswordChanged(val password: String) : RegisterUiEvent
    data object RegisterClicked : RegisterUiEvent
}

// Efectos de UI
sealed interface RegisterUiEffect {
    data object NavigateToHome : RegisterUiEffect
    data class ShowError(val message: String) : RegisterUiEffect
}