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
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.repository.AuthRepository
import com.uvg.uvgeats.data.repository.AuthRepositoryImpl

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    // estado privado mutable
    private val _uiState = MutableStateFlow(LoginUiState())
    // estado publico inmutable
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // canal para efectos
    private val _uiEffect = Channel<LoginUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    // manejo de eventos
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

        // validación basica
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            viewModelScope.launch {
                _uiEffect.send(LoginUiEffect.ShowError("Por favor completa todos los campos"))
            }
            return
        }

        // validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            viewModelScope.launch {
                _uiEffect.send(LoginUiEffect.ShowError("Email inválido"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.login(currentState.email, currentState.password)) {
                is Result.Success<*> -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(LoginUiEffect.NavigateToHome)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val errorMessage = when {
                        result.exception.message?.contains("password") == true ->
                            "Contraseña incorrecta"
                        result.exception.message?.contains("user") == true ->
                            "Usuario no encontrado"
                        result.exception.message?.contains("network") == true ->
                            "Error de conexión"
                        else -> "Error al iniciar sesión: ${result.exception.message}"
                    }
                    _uiEffect.send(LoginUiEffect.ShowError(errorMessage))
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
