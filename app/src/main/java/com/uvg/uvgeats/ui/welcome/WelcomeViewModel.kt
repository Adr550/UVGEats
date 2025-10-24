package com.uvg.uvgeats.ui.welcome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: WelcomeUiEvent) {
        when (event) {
            is WelcomeUiEvent.NavigateToLogin -> {
                // La navegación se maneja en el composable
            }
            is WelcomeUiEvent.NavigateToRegister -> {
                // La navegación se maneja en el composable
            }
        }
    }
}