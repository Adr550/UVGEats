package com.uvg.uvgeats.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<RegisterUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is RegisterUiEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.password,
                        passwordsMatch = event.password == it.repeatPassword || it.repeatPassword.isEmpty()
                    )
                }
            }
            is RegisterUiEvent.RepeatPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        repeatPassword = event.password,
                        passwordsMatch = event.password == it.password
                    )
                }
            }
            is RegisterUiEvent.RegisterClicked -> {
                performRegister()
            }
        }
    }

    private fun performRegister() {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            viewModelScope.launch {
                _uiEffect.send(RegisterUiEffect.ShowError("Por favor completa todos los campos"))
            }
            return
        }

        if (!currentState.passwordsMatch) {
            viewModelScope.launch {
                _uiEffect.send(RegisterUiEffect.ShowError("Las contraseñas no coinciden"))
            }
            return
        }

        if (currentState.password.length < 6) {
            viewModelScope.launch {
                _uiEffect.send(RegisterUiEffect.ShowError("La contraseña debe tener al menos 6 caracteres"))
            }
            return
        }

        // Simular registro
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            kotlinx.coroutines.delay(1000)

            _uiState.update { it.copy(isLoading = false) }
            _uiEffect.send(RegisterUiEffect.NavigateToHome)
        }
    }
}