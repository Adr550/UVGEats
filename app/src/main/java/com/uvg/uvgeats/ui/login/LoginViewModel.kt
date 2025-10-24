package com.uvg.uvgeats.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Estado privado mutable
    private val _uiState = MutableStateFlow(LoginUiState())
    // Estado público inmutable
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Canal para efectos one-time (navegación)
    private val _uiEffect = Channel<LoginUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    // Manejo de eventos
    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is LoginUiEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is LoginUiEvent.LoginClicked -> {
                performLogin()
            }
            is LoginUiEvent.ForgotPasswordClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(LoginUiEffect.NavigateToForgotPassword)
                }
            }
        }
    }

    private fun performLogin() {
        val currentState = _uiState.value

        // Validación básica
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            viewModelScope.launch {
                _uiEffect.send(LoginUiEffect.ShowError("Por favor completa todos los campos"))
            }
            return
        }

        // Simular login (aquí iría tu lógica de autenticación real)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simular delay de red
            kotlinx.coroutines.delay(1000)

            // Validación simple para demo
            if (currentState.email.contains("@")) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(LoginUiEffect.NavigateToHome)
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(LoginUiEffect.ShowError("Credenciales inválidas"))
            }
        }
    }
}