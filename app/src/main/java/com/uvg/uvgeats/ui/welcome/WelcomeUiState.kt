package com.uvg.uvgeats.ui.welcome

// UiState inmutable para WelcomeScreen
data class WelcomeUiState(
    val isLoading: Boolean = false
)
// Eventos de UI
sealed interface WelcomeUiEvent {
    data object NavigateToLogin : WelcomeUiEvent
    data object NavigateToRegister : WelcomeUiEvent
}
