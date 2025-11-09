package com.uvg.uvgeats.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.repository.AuthRepository
import com.uvg.uvgeats.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

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
                _uiEffect.send(RegisterUiEffect.ShowError("completa todos los campos"))
            }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            viewModelScope.launch {
                _uiEffect.send(RegisterUiEffect.ShowError("Email invalido"))
            }
            return
        }

        if (!currentState.passwordsMatch) {
            viewModelScope.launch {
                _uiEffect.send(RegisterUiEffect.ShowError("Las contraseñas no coinciden"))
            }
            return
        }

        // Registro con firebase
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.register(currentState.email, currentState.password)) {
                is Result.Success<*> -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(RegisterUiEffect.NavigateToHome)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val errorMessage = when {
                        result.exception.message?.contains("already in use") == true ->
                            "Este email ya está registrado"
                        result.exception.message?.contains("weak-password") == true ->
                            "La contraseña es muy débil"
                        result.exception.message?.contains("invalid-email") == true ->
                            "Email inválido"
                        result.exception.message?.contains("network") == true ->
                            "Error de conexión"
                        else -> "Error al registrar: ${result.exception.message}"
                    }
                    _uiEffect.send(RegisterUiEffect.ShowError(errorMessage))
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}